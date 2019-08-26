package g2sysnet.smart_gw

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.core.view.GravityCompat
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_home_page.*
import kotlinx.android.synthetic.main.app_bar_home_page.*
import kotlinx.android.synthetic.main.custom_confirm_dialog.view.*

class HomePage : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            //region 취소전묻기
            val alertDialog = LayoutInflater.from(this).inflate(R.layout.custom_confirm_dialog, null)
            val mBuilder = AlertDialog.Builder(this)
                .setView(alertDialog)
            //show dialog
            val  mAlertDialog = mBuilder.show()

            alertDialog.dialog_yes.setOnClickListener{
                val intent=Intent(this@HomePage, MenuPage::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
            }

            alertDialog.dialog_no.setOnClickListener{
                mAlertDialog.dismiss()
            }
        }
    }

//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        menuInflater.inflate(R.menu.home_page, menu)
//        return true
//    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.shipping_list -> {
                startActivity(Intent(this@HomePage, DeliveryList::class.java))
            }
            R.id.as_allocation_list -> {
                startActivity(Intent(this@HomePage, AsAllocationList::class.java))
            }
            R.id.exit_from_erp  -> {
                //region 취소전묻기
                val alertDialog = LayoutInflater.from(this).inflate(R.layout.custom_confirm_dialog, null)
                val mBuilder = AlertDialog.Builder(this)
                    .setView(alertDialog)
                //show dialog
                val  mAlertDialog = mBuilder.show()

                alertDialog.dialog_yes.setOnClickListener{
                    val intent=Intent(this@HomePage, MenuPage::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
                }

                alertDialog.dialog_no.setOnClickListener{
                    mAlertDialog.dismiss()
                }
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
}
