package com.example.expensenote_24

import android.app.DatePickerDialog
import android.app.SyncNotedAppOp
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.expensenote_24.adapter.ExpenseAdapter
import com.example.expensenote_24.databinding.ActivityMainBinding
import com.example.expensenote_24.databinding.DialogAddExpenseBinding
import com.example.expensenote_24.db.ExpenseDatabaseHelper
import com.example.expensenote_24.model.ExpenseItem
import com.google.android.material.button.MaterialButtonToggleGroup
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainActivity : AppCompatActivity() {
    //สร้างตัวแปรสำหรับ UI
    private lateinit var etAmount: EditText
    private lateinit var etNote: EditText
    private lateinit var btnAdd: Button
    private lateinit var btnClear: Button
    private lateinit var tvLastItem: TextView
    private lateinit var tvTotal: TextView

    //ตัวแปรสำหรับเก็บค่ายอดรวมทั้งหมด
    private var total: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        //เชื่อมโยงตัวแปร เข้ากับ ID ในหน้า XML
        etAmount = findViewById<EditText>(R.id.etAmount)
        etNote = findViewById<EditText>(R.id.etNote)
        btnAdd = findViewById<Button>(R.id.btnAdd)
        btnClear = findViewById<Button>(R.id.btnClear)
        tvTotal = findViewById<TextView>(R.id.tvTotal)
        tvLastItem = findViewById<TextView>(R.id.tvLastItem)

        //กำหนดการทำงานเมื่อผู้ใช้ทำการกดปุ่มเพิ่มรายการ
        btnAdd.setOnClickListener {
            //สร้างตัวแปรที่เอาไว้เก็บข้อมูลจกาช่อง
            val amountText = etAmount.text.toString().trim()
            val noteText = etNote.text.toString().trim()

            //ทำการตรวจสอบว่าผู้ใช้ทำการกรอกจำนวนเงินมาแล้วหรือยัง
            if (amountText.isEmpty()) {
                Toast.makeText(this, "กรุณากรอกจำนวนเงิน", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val amount = amountText.toDoubleOrNull()
            if (amount == null) {
                //แจ้งเตือนผู้ใช้ว่าให้กรอกตัวเลขมา ไม่ใช่ตัวหนังสือ
                Toast.makeText(this, "กรุณากรอกเฉพาะตัวเลข ในช่องกรอกจำนวนเงิน", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //เอาจำนวนมารวมกัน
            total += amount

            //ตรวจสอบว่ามีการกรอกคำอธิบายมั้ย ถ้าไม่มี ให้เป็นคำว่า "ไม่ระบุ"
            val showNote = if (noteText.isNotEmpty()) "ไม่ระบุ" else noteText

            tvLastItem.text = "รายการล่าสุด: $showNote - %.2f บาท".format(amount)
            tvTotal.text = "ยอดรวมวันนี้: %.2f บาท".format(total)

            etAmount.text.clear()
            etNote.text.clear()
        }
        btnClear.setOnClickListener {
            etAmount.text.clear()
            etNote.text.clear()

            tvLastItem.text = "รายการล่าสุด: -"
            tvTotal.text = "ยอดรวมวันนี้: 0.00 บาท"

            total = 0.0
        }
    }
}