package com.example.qrcodeapp

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.qrcodeapp.ui.theme.QRCodeAppTheme
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private var textResult = mutableStateOf("")

    private val barcodeLauncher = registerForActivityResult((ScanContract())) { result ->
        result.contents?.let {
            textResult.value = result.contents
        } ?: run {
            Toast.makeText(this, "Cancelado", Toast.LENGTH_SHORT).show()
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) showCamera()
    }

    private fun showCamera() {
        barcodeLauncher.launch(
            ScanOptions().apply {
                setDesiredBarcodeFormats(ScanOptions.QR_CODE)
                setPrompt("Scan QRCode")
                setCameraId(0)
                setBeepEnabled(false)
                setOrientationLocked(false)
            }
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            QRCodeAppTheme {
                BuildScreen()
            }
        }
    }

    @Composable
    @Preview
    private fun BuildScreen() {
        Scaffold(
            bottomBar = {
                BottomAppBar(
                    actions = {},
                    floatingActionButton = {
                        FloatingActionButton(
                            onClick = { checkCameraPermissions(this) }
                        ) {
                            Icon(
                                painter = painterResource(
                                    id = R.drawable.qr_code_scan
                                ),
                                contentDescription = "QR Code"
                            )
                        }
                    }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(
                        id = R.drawable.qr_code_scan
                    ),
                    contentDescription = "QR Code Big Size",
                    modifier = Modifier
                        .size(100.dp)
                        .clickable {
                            checkCameraPermissions(this@MainActivity)
                        },
                )
                Text(
                    text = textResult.value,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold
                )
            }

        }
    }

    private fun checkCameraPermissions(context: Context) {
        if (ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            showCamera()
        } else if (shouldShowRequestPermissionRationale(android.Manifest.permission.CAMERA)) {
            Toast.makeText(this, "Permissão da camera obrigatória", Toast.LENGTH_SHORT).show()
            lifecycleScope.launch {
                delay(1500)
                requestPermissionLauncher.launch(android.Manifest.permission.CAMERA)
            }
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.CAMERA)
        }
    }
}
