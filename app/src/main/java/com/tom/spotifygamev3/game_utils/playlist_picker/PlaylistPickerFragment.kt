package com.tom.spotifygamev3.game_utils.playlist_picker

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment
import com.tom.spotifygamev3.Utils.Constants
import com.tom.spotifygamev3.databinding.PlaylistPickerFragmentBinding
import java.lang.IllegalArgumentException

class PlaylistPickerFragment : Fragment() {

    private val TAG = "PlaylistPickerFragment"

    private val viewModel: PlaylistPickerViewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[PlaylistPickerViewModel::class.java]
    }

    private lateinit var viewModelFactory: PlaylistPickerViewModelFactory

//    val adapter = PlaylistRecyclerAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = PlaylistPickerFragmentBinding.inflate(inflater)

        viewModelFactory =
            PlaylistPickerViewModelFactory(
                requireActivity().application,
                PlaylistPickerFragmentArgs.fromBundle(requireArguments()).gameType
            )

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        val adapter = PlaylistAdapter(PlaylistListener { playlistId ->
            // navigate to game fragment
            viewModel.onPlaylistChosen(playlistId)
        })

        viewModel.navigateToGame.observe(viewLifecycleOwner, Observer { playlistId ->
            Log.d(TAG, playlistId ?: "nulled")
            playlistId?.let {

                val action = when (viewModel.gameType.value) {
                    Constants.ALBUM_GAME_TYPE ->
                        PlaylistPickerFragmentDirections.actionPlaylistPickerFragmentToAlbumGameFragment(
                            playlistId = playlistId
                        )
                    Constants.HIGH_LOW_GAME_TYPE ->
                        PlaylistPickerFragmentDirections.actionPlaylistPickerFragmentToHighLowGameFragment(
                            playlistId
                        )
                    else -> throw IllegalArgumentException("Unknown Game Type")
                }
                NavHostFragment.findNavController(this).navigate(action)
                viewModel.onNavigationToGame()
            }
        })

        binding.playlistRv.adapter = adapter

        viewModel.userPlaylists.observe(viewLifecycleOwner, Observer { playlists ->
            adapter.submitPlaylist(playlists)
        })

        return binding.root
    }


}