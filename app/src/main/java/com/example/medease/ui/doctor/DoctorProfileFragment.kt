import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.medease.ui.login.LoginActivity
import com.example.medease.R

class DoctorProfileFragment : Fragment() {

    private lateinit var btnLogout: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate layout
        return inflater.inflate(R.layout.fragment_doctor_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnLogout = view.findViewById(R.id.btn_logout)

        btnLogout.setOnClickListener {
            // 2️⃣ Navigasi ke LoginActivity dan hapus stack

        }
    }
}