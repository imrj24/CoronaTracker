package rahul.j.coronatrackerlive

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar

class MainActivity : AppCompatActivity(), ToolbarSettable {

    override lateinit var toolbar: Toolbar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setToolbar()
    }

    private fun setToolbar() {
        toolbar = findViewById(R.id.default_toolbar)
        setSupportActionBar(toolbar)
    }
}