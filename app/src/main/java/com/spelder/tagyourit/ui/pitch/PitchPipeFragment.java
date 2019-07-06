package com.spelder.tagyourit.ui.pitch;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.spelder.tagyourit.R;
import com.spelder.tagyourit.pitch.Pitch;
import com.spelder.tagyourit.pitch.PitchPlayer;

/** Fragment used to display the pitch pipe. */
public class PitchPipeFragment extends Fragment {
  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    Log.d("BrowseFragment", "onCreateView");
    View view = inflater.inflate(R.layout.pitch_pipe, container, false);

    Button centerPitch = view.findViewById(R.id.pitch_pipe_center);
    centerPitch.setOnClickListener(v -> PitchPlayer.togglePitch(getContext()));

    PitchButton pitchA = view.findViewById(R.id.pitch_pipe_a);
    pitchA.setCenterPitchButton(centerPitch);
    pitchA.setPitch(Pitch.A);

    PitchButton pitchBb = view.findViewById(R.id.pitch_pipe_bb);
    pitchBb.setCenterPitchButton(centerPitch);
    pitchBb.setPitch(Pitch.B_FLAT);

    PitchButton pitchB = view.findViewById(R.id.pitch_pipe_b);
    pitchB.setCenterPitchButton(centerPitch);
    pitchB.setPitch(Pitch.B);

    PitchButton pitchC = view.findViewById(R.id.pitch_pipe_c);
    pitchC.setCenterPitchButton(centerPitch);
    pitchC.setPitch(Pitch.C);

    PitchButton pitchDb = view.findViewById(R.id.pitch_pipe_db);
    pitchDb.setCenterPitchButton(centerPitch);
    pitchDb.setPitch(Pitch.D_FLAT);

    PitchButton pitchD = view.findViewById(R.id.pitch_pipe_d);
    pitchD.setCenterPitchButton(centerPitch);
    pitchD.setPitch(Pitch.D);

    PitchButton pitchEb = view.findViewById(R.id.pitch_pipe_eb);
    pitchEb.setCenterPitchButton(centerPitch);
    pitchEb.setPitch(Pitch.E_FLAT);

    PitchButton pitchE = view.findViewById(R.id.pitch_pipe_e);
    pitchE.setCenterPitchButton(centerPitch);
    pitchE.setPitch(Pitch.E);

    PitchButton pitchF = view.findViewById(R.id.pitch_pipe_f);
    pitchF.setCenterPitchButton(centerPitch);
    pitchF.setPitch(Pitch.F);

    PitchButton pitchGb = view.findViewById(R.id.pitch_pipe_gb);
    pitchGb.setCenterPitchButton(centerPitch);
    pitchGb.setPitch(Pitch.G_FLAT);

    PitchButton pitchG = view.findViewById(R.id.pitch_pipe_g);
    pitchG.setCenterPitchButton(centerPitch);
    pitchG.setPitch(Pitch.G);

    PitchButton pitchAb = view.findViewById(R.id.pitch_pipe_ab);
    pitchAb.setCenterPitchButton(centerPitch);
    pitchAb.setPitch(Pitch.A_FLAT);

    return view;
  }
}
