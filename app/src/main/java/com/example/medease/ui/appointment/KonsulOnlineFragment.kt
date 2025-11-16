package com.example.medease.ui.appointment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.medease.R
import com.example.medease.databinding.FragmentKonsulOnlineBinding
import androidx.navigation.fragment.findNavController


class KonsulOnlineFragment : Fragment() {

    private lateinit var binding: FragmentKonsulOnlineBinding
    private val viewModel: ChatListViewModel by viewModels()
    private lateinit var adapter: ChatListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentKonsulOnlineBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = ChatListAdapter(emptyList()) { item ->
            val action = KonsulOnlineFragmentDirections
                .actionKonsulOnlineFragmentToChatRoomFragment(item.id)

            findNavController().navigate(action)
        }

        binding.recyclerChatList.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerChatList.adapter = adapter

        viewModel.chatList.observe(viewLifecycleOwner) { list ->
            adapter.update(list)
        }
    }
}
