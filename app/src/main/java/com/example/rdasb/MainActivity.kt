package com.example.rdasb

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.deck_layout.view.*
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.*
import java.util.*


class MainActivity : AppCompatActivity() {
    private val boosterList = arrayOf("PETN/TNT(50/50)", "Commercial booster w/o wrapper")
    private val workingAreaList = arrayOf("OBR1", "OBR2", "OBR3", "OBR4", "OBR5", "COAL1", "COAL2", "COAL3", "COAL4")
    private val typeOfChargeList = arrayOf("ANFO(94/6) - SOLAR", "ANFO(94/6) with 5% AI - SOLAR", "ANFO(92/8) - SOLAR", "Emulsion A - SOLAR", "Emulsion B-10 Poly - SOLAR",
                                        "Emulsion B-0 Poly - SOLAR", "Emulsion B-5 cbdb - SOLAR", "Emulsion B-10 cbdb - SOLAR", "Emulsion B-0 cbdb - SOLAR",
                                        "Watergels - SOLAR", "ANFO(94/6) - IOCL", "ANFO(94/6) with 5% AI - IOCL", "ANFO(92/8) - IOCL", "Emulsion A - IOCL", "Emulsion B-10 Poly - IOCL",
                                        "Emulsion B-0 Poly - IOCL", "Emulsion B-5 cbdb - IOCL", "Emulsion B-10 cbdb - IOCL", "Emulsion B-0 cbdb - IOCL","Watergels - IOCL",
                                        "ANFO(94/6) - IDL", "ANFO(94/6) with 5% AI - IDL", "ANFO(92/8) - IDL", "Emulsion A - IDL", "Emulsion B-10 Poly - IDL",
                                        "Emulsion B-0 Poly - IDL", "Emulsion B-5 cbdb - IDL", "Emulsion B-10 cbdb - IDL", "Emulsion B-0 cbdb - IDL",
                                        "Watergels - IDL")
    private val excelHeaderList = arrayOf("S No", "Mine ID", "Date", "Working Area", "Hole Diameter(mm)", "Drilling Deviation(m)", "Bench Height(m)", "Sub Drilling(m)", "Hole Length(m)",
                                            "Burden(m)", "Spacing(m)", "No of holes", "No of rows", "No of deck", "Deck1 Charge(kg)", "Type of charge1", "Deck1 Stemming(m)", "Deck2 Charge(kg)",
                                      "Type of charge2", "Deck2 Stemming(m)", "Deck3 Charge(kg)", "Type of charge3", "Deck3 Stemming(m)", "Deck4 Charge(kg)", "Type of charge4", "Deck4 Stemming(m)",
                                          "Deck5 Charge(kg)", "Type of charge5", "Deck5 Stemming(m)", "Total charge in the round(kg)", "Total Booster", "Type of Booster", "Total Charge of Boosters(kg)",
                                        "NONEL DTH(m)", "NONEL TLD(m)", "NONEL COMBO(m)", "Electronic DET", "DF(m)", "SF(m)", "OD", "ED")

    private var outputList = arrayListOf<String>()

    private var mYear = 0
    private var mMonth = 0
    private var mDay = 0

    private val fileName = "RDASB.xlsx"
    private val folderName = "Pradeep"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView();
    }

    private fun initView() {
        initDatePicker()
        initWorkingArea()
        initDeckPicker()
        initBooster()
        initSaveButton()
        initFiles()
    }

    private fun initFiles() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((this as Activity?)!!, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),1)
        }
        val extStorageDirectory = Environment.getExternalStorageDirectory().toString()
        val folder = File(extStorageDirectory + File.separator + folderName)
        if (!folder.exists()) {
            folder.mkdir()
        }
        val readFile = File(extStorageDirectory + File.separator + folderName, fileName)
        if(!readFile.exists()) {
            try {
                readFile.createNewFile()
                val myWorkBook = XSSFWorkbook()
                val mySheet = myWorkBook.createSheet("pradeep")
                val rowNum = 0
                val row = mySheet.createRow(rowNum)
                for (ind in excelHeaderList.indices) {
                    row.createCell(ind).setCellValue(excelHeaderList[ind])
                }
                try {
                    val fileOut = FileOutputStream(readFile) //Opening the file
                    myWorkBook.write(fileOut) //Writing all your row column inside the file
                    fileOut.close() //closing the file and done
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                }
            } catch (e1: IOException) {
                e1.printStackTrace()
            }
        }
//        val readFile = File(extStorageDirectory + File.separator + folderName + File.separator + fileName)
    }

    private fun initBooster() {
        typeOfBoosterSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                Toast.makeText(applicationContext, boosterList[position], Toast.LENGTH_LONG).show()
            }

        }
        val typeOfBoosterAdapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, boosterList)
        typeOfBoosterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        typeOfBoosterSpinner.adapter = typeOfBoosterAdapter
    }

    private fun initWorkingArea() {
        workingArea.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                Toast.makeText(applicationContext, workingAreaList[position], Toast.LENGTH_LONG).show()
            }

        }
        val workingAreaAdapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, workingAreaList)
        workingAreaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        workingArea.adapter = workingAreaAdapter
    }

    private fun initDatePicker() {
        val c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        dateSelector.setText("$mDay/$mMonth/$mYear")
        val datePickerDialog = DatePickerDialog(
            this,
            OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                dateSelector.setText("$dayOfMonth/${(monthOfYear + 1)}/$year")
            }, mYear, mMonth, mDay
        )

        dateSelector.setOnClickListener {
            if (!datePickerDialog.isShowing)    datePickerDialog.show()
        }

        dateSelector.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (hasFocus && !datePickerDialog.isShowing)    datePickerDialog.show()
        }
    }

    private fun initDeckPicker() {
        noOfDeckEditText.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.toString().isNotEmpty() && s.toString().toInt() in 1..5) {
                    initDeckLayout(s.toString().toInt())
                } else {
                    initDeckLayout(0)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        })
    }

    private fun initDeckLayout(deckNumber: Int) {
        val deckLayoutList = listOf(deckOneLayout, deckTwoLayout, deckThreeLayout, deckFourLayout, deckFiveLayout)
        for (i in 1..5) {
            if (i > deckNumber) deckLayoutList[i-1].visibility = View.GONE
            else {
                deckLayoutList[i - 1].visibility = View.VISIBLE
                setDeckLayoutContent(deckLayoutList[i-1], i)
            }
        }
    }

    private fun setDeckLayoutContent(view: View, deckNo: Int) {
        val typeChargeSpinner = view.findViewById<Spinner>(R.id.deckChargeSpinner)
        val deckText = view.findViewById<TextView>(R.id.deckText)
        val deckEditText = view.findViewById<EditText>(R.id.deckEditText)
        val typeOfChargeText = view.findViewById<TextView>(R.id.typeOfChargeText)
        val deckStemmingText = view.findViewById<TextView>(R.id.deckStemmingText)
        val deckStemmingEditText = view.findViewById<EditText>(R.id.deckStemmingEditText)

        deckText.text = "Deck$deckNo Charge\n(kg)"
        deckEditText.hint = "Enter value of deck charge$deckNo"
        typeOfChargeText.text = "Type of charge$deckNo"
        deckStemmingText.text = "Deck$deckNo Stemming\n(m)"
        deckStemmingEditText.hint = "Enter value of deck$deckNo stemming"

        typeChargeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                Toast.makeText(applicationContext, typeOfChargeList[position], Toast.LENGTH_LONG).show()
            }

        }
        val typeChargeAdapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, typeOfChargeList)
        typeChargeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        typeChargeSpinner.adapter = typeChargeAdapter
    }

    private fun initSaveButton() {
        saveButton.setOnClickListener(View.OnClickListener {
            if (!isExternalStorageAvailable() || isExternalStorageReadOnly())
            {
                Log.w("FileUtils", "Storage not available or read only");
                return@OnClickListener;
            }
            initFiles()
            generateOutput()
            try {
                val readFile = File(Environment.getExternalStorageDirectory().toString() + File.separator + folderName, fileName)
                val myWorkBook = XSSFWorkbook(FileInputStream(readFile))
                val mySheet = myWorkBook.getSheet("Pradeep")

                val rowNum = mySheet.lastRowNum + 1

                val row = mySheet.createRow(rowNum)
                row.createCell(0).setCellValue(rowNum.toString())
                for (ind in outputList.indices) {
                    row.createCell(ind+1).setCellValue(outputList[ind])
                }
                try {
                    val fileOut = FileOutputStream(readFile) //Opening the file
                    myWorkBook.write(fileOut) //Writing all your row column inside the file
                    fileOut.close() //closing the file and done
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        })
    }

    private fun generateOutput() {
        outputList.clear()
        outputList.add(mineIdEditText.text.toString())
        outputList.add(dateSelector.text.toString())
        outputList.add(workingArea.selectedItem.toString())
        outputList.add(holeDiameterEditText.text.toString())
        outputList.add(drillingDeviationEditText.text.toString())
        outputList.add(benchHeightEditText.text.toString())
        outputList.add(subDrillLengthEditText.text.toString())
        outputList.add(holeLengthEditText.text.toString())
        outputList.add(burdenEditText.text.toString())
        outputList.add(spacingEditText.text.toString())
        outputList.add(noOfHolesEditText.text.toString())
        outputList.add(noOfRowsEditText.text.toString())
        outputList.add(noOfDeckEditText.text.toString())

        val deckLayoutList = listOf(deckOneLayout, deckTwoLayout, deckThreeLayout, deckFourLayout, deckFiveLayout)

        for(i in deckLayoutList.indices) {
            if (deckLayoutList[i].visibility == View.VISIBLE){
                outputList.add(deckLayoutList[i].deckEditText.text.toString())
                outputList.add(deckLayoutList[i].deckChargeSpinner.selectedItem.toString())
                outputList.add(deckLayoutList[i].deckStemmingEditText.text.toString())
            }
            else {
                outputList.add("")
                outputList.add("")
                outputList.add("")
            }
        }

        outputList.add(totalChargeInTheRoundEditText.text.toString())
        outputList.add(totalBoosterEditText.text.toString())
        outputList.add(typeOfBoosterSpinner.selectedItem.toString())
        outputList.add(totalChargeOfBoostersEditText.text.toString())
        outputList.add(noNelDthEditText.text.toString())
        outputList.add(noNelTldEditText.text.toString())
        outputList.add(noNelComboEditText.text.toString())
        outputList.add(electronicDetEditText.text.toString())
        outputList.add(dfEditText.text.toString())
        outputList.add(sfEditText.text.toString())
        outputList.add(odEditText.text.toString())
        outputList.add(edEditText.text.toString())
    }







    private fun isExternalStorageReadOnly(): Boolean {
        val extStorageState: String = Environment.getExternalStorageState()
        return Environment.MEDIA_MOUNTED_READ_ONLY == extStorageState
    }

    private fun isExternalStorageAvailable(): Boolean {
        val extStorageState: String = Environment.getExternalStorageState()
        return Environment.MEDIA_MOUNTED == extStorageState
    }
    companion object {
        init {
            System.setProperty(
                "org.apache.poi.javax.xml.stream.XMLInputFactory",
                "com.fasterxml.aalto.stax.InputFactoryImpl"
            )
            System.setProperty(
                "org.apache.poi.javax.xml.stream.XMLOutputFactory",
                "com.fasterxml.aalto.stax.OutputFactoryImpl"
            )
            System.setProperty(
                "org.apache.poi.javax.xml.stream.XMLEventFactory",
                "com.fasterxml.aalto.stax.EventFactoryImpl"
            )
        }
    }
}
