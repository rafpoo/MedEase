package com.example.medease.ui.appointment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.medease.databinding.FragmentChatRoomBinding

class ChatRoomFragment : Fragment() {

    private lateinit var binding: FragmentChatRoomBinding
    private val viewModel: ChatRoomViewModel by activityViewModels()

    private lateinit var adapter: ChatMessageAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChatRoomBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val chatId = arguments?.getString("chatId") ?: ""

        // Setup RecyclerView
        adapter = ChatMessageAdapter(emptyList())
        binding.recyclerChat.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerChat.adapter = adapter

        // Observe messages dari ViewModel
        viewModel.messages.observe(viewLifecycleOwner) { list ->
            adapter.updateData(list)
            // scroll ke pesan terakhir
            binding.recyclerChat.scrollToPosition(list.size - 1)
        }

        // Setup tombol send
        binding.btnSend.setOnClickListener {
            val text = binding.etMessage.text.toString()
            if (text.isNotBlank()) {
                viewModel.sendMessage(text)
                binding.etMessage.text.clear()
            }
        }
    }
}
