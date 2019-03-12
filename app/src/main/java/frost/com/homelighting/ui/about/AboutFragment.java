package frost.com.homelighting.ui.about;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import frost.com.homelighting.R;

public class AboutFragment extends Fragment {

    private TextView mAboutText;

    public static AboutFragment newInstance() {
        return new AboutFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);
//        mAboutText = view.findViewById(R.id.about_text);
//        mAboutText.setMovementMethod(LinkMovementMethod.getInstance());
        return inflater.inflate(R.layout.fragment_about, container, false);
    }
}
