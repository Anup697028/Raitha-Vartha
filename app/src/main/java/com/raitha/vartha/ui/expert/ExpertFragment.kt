package com.raitha.vartha.ui.expert

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.speech.RecognizerIntent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.raitha.vartha.databinding.FragmentExpertBinding
import com.raitha.vartha.utils.Constants
import com.raitha.vartha.utils.TextToSpeechManager
import com.raitha.vartha.viewmodel.AIViewModel
import com.raitha.vartha.viewmodel.AIViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class ExpertFragment : Fragment() {
    private var _binding: FragmentExpertBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AIViewModel by viewModels {
        AIViewModelFactory(Constants.GEMINI_API_KEY)
    }

    private lateinit var chatAdapter: ChatAdapter
    private lateinit var ttsManager: TextToSpeechManager
    private var currentPhotoPath: String? = null

    private val voiceLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val spokenText = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.get(0)
            spokenText?.let {
                binding.etMessage.setText(it)
                viewModel.sendMessage(it)
            }
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        if (permissions[Manifest.permission.CAMERA] == true) {
            openCamera()
        }
    }

    private val captureImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            currentPhotoPath?.let { path ->
                val bitmap = BitmapFactory.decodeFile(path)
                viewModel.analyzeCropImage(bitmap)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentExpertBinding.inflate(inflater, container, false)
        ttsManager = TextToSpeechManager(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupListeners()
        observeData()
    }

    private fun setupRecyclerView() {
        chatAdapter = ChatAdapter()
        binding.rvChat.apply {
            adapter = chatAdapter
            layoutManager = LinearLayoutManager(requireContext()).apply {
                stackFromEnd = true
            }
        }
    }

    private fun setupListeners() {
        binding.btnSend.setOnClickListener {
            val text = binding.etMessage.text.toString()
            if (text.isNotBlank()) {
                viewModel.sendMessage(text)
                binding.etMessage.setText("")
            }
        }

        binding.btnCamera.setOnClickListener {
            checkCameraPermission()
        }
        
        // Add voice input to the chatbot
        binding.etMessage.setCompoundDrawablesWithIntrinsicBounds(0, 0, android.R.drawable.ic_btn_speak_now, 0)
        binding.etMessage.setOnTouchListener { _, event ->
            val DRAWABLE_RIGHT = 2
            if (event.action == android.view.MotionEvent.ACTION_UP) {
                if (event.rawX >= (binding.etMessage.right - binding.etMessage.compoundDrawables[DRAWABLE_RIGHT].bounds.width())) {
                    startVoiceInput()
                    return@setOnTouchListener true
                }
            }
            false
        }
    }

    private fun startVoiceInput() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now...")
        }
        try {
            voiceLauncher.launch(intent)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Voice input not supported", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            openCamera()
        } else {
            requestPermissionLauncher.launch(arrayOf(Manifest.permission.CAMERA))
        }
    }

    private fun openCamera() {
        val photoFile: File? = try {
            createImageFile()
        } catch (ex: Exception) {
            null
        }

        photoFile?.also {
            val photoUri: Uri = FileProvider.getUriForFile(requireContext(), "com.raitha.vartha.fileprovider", it)
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
            }
            captureImageLauncher.launch(intent)
        }
    }

    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir).apply {
            currentPhotoPath = absolutePath
        }
    }

    private fun observeData() {
        lifecycleScope.launch {
            viewModel.messages.collectLatest { messages ->
                chatAdapter.submitList(messages) {
                    if (messages.isNotEmpty()) {
                        binding.rvChat.scrollToPosition(messages.size - 1)
                        val lastMessage = messages.last()
                        if (!lastMessage.isUser) {
                            ttsManager.speak(lastMessage.text)
                        }
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.isTyping.collectLatest { isTyping ->
                binding.progressBar.visibility = if (isTyping) View.VISIBLE else View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        ttsManager.shutdown()
        _binding = null
    }
}
