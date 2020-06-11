package com.geespace.hotfix

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.tencent.bugly.beta.Beta
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

class MainActivity : AppCompatActivity() {

    private var permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE)

    var REQUEST_PERMISSION=100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var lackPermission= ArrayList<String>()

        for(request in permissions){
            if(ContextCompat.checkSelfPermission(this,request)==PackageManager.PERMISSION_DENIED){
                lackPermission.add(request)
            }
        }
        if(lackPermission.size>0){
            var arrayLack= arrayOfNulls<String>(lackPermission.size)

            ActivityCompat.requestPermissions(this,lackPermission.toArray(arrayLack),REQUEST_PERMISSION)
        }else{
            setListener()
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        var permissionGranted=true
        if(requestCode==REQUEST_PERMISSION){
            for(grantResult in grantResults){
                if(grantResult==PackageManager.PERMISSION_DENIED){
                    permissionGranted=false
                    break
                }
            }
        }

        if(permissionGranted){
           setListener()
        }
    }

    fun setListener(){
        tv_fix.setOnClickListener {
            Beta.checkHotFix()
//                FixDexUtils.loadFixedDex(this, File("/mnt/sdcard/classes.dex"))
        }

        tv_caclulate.setOnClickListener {
            Log.e("MainActivity",LoadBugClass.getBugString())
            Toast.makeText(this,LoadBugClass.getBugString(),1).show()
            startActivity(Intent(this,HotPlugActivity::class.java))
        }
    }
}
