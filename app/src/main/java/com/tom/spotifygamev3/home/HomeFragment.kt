package com.tom.spotifygamev3.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.tom.spotifygamev3.databinding.ChooseGameFragmentBinding
import com.tom.spotifygamev3.utils.Constants

class HomeFragment : Fragment() {

    private val viewModel: HomeViewModel by lazy {
        ViewModelProvider(this).get(HomeViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = ChooseGameFragmentBinding.inflate(inflater)

        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        viewModel.navigateToAlbumGame.observe(viewLifecycleOwner, Observer { navigate ->
            if (navigate) {
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToPlaylistPickerFragment(
                    Constants.ALBUM_GAME_TYPE))
                viewModel.onNavigateToAlbumGame()
            }
        })

        viewModel.navigateToHighLow.observe(viewLifecycleOwner, Observer { navigate ->
            if (navigate) {
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToPlaylistPickerFragment(
                    Constants.HIGH_LOW_GAME_TYPE
                ))
                viewModel.onNavigateToHighLow()
            }
        })

        viewModel.navigateToBeatIntro.observe(viewLifecycleOwner, Observer { navigate ->
            if (navigate) {
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToPlaylistPickerFragment(
                    Constants.BEAT_INTRO_GAME_TYPE
                ))
                viewModel.onNavigateToBeatIntro()
            }
        })

        return binding.root

    }

}