package com.vhaa.practica1

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.vhaa.practica1.databinding.ActivityMainBinding
import helpers.Date
import helpers.hideKeyboard
import java.text.DecimalFormat

/**
 * MainActivity
 *
 * @author Victor Hugo Aguilar Aguilar
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnCalcular.setOnClickListener {
            if (allFieldFilled() && validateDate() && validateNotes()) {
                val df = DecimalFormat("#.00")
                val noteFinal = evaluate()
                binding.lblResult.text = df.format(noteFinal)
                binding.lblResultDescripcion.text = obtainDescriptionNote(noteFinal)
                it.hideKeyboard()
            }
        }
    }


    /**
     * Obtener una descripción de la nota obtenida
     *
     * @return String con la descripcion del resultado de la nota final
     */
    private fun obtainDescriptionNote(noteFinal: Float): String {
        var description = ""
        if (noteFinal < 5) {
            description = getString(R.string.note_description_insuficient)
        }
        if (noteFinal >= 5 && noteFinal < 6) {
            description = getString(R.string.note_description_suficient)
        }
        if (noteFinal >= 6 && noteFinal < 9.5) {
            description = getString(R.string.note_description_good)
        }
        if (noteFinal >= 9.5) {
            description = getString(R.string.note_description_excelent)
        }
        return description
    }

    /**
     * Evalua las notas según los criterios
     *
     * Si el alumno pertenece a semipresencial su nota final será la nota media de las tres
     * evaluaciones.
     *
     * Si el alumno pertenece a presencial su nota final será la nota media, siempre y cuando
     * haya aprobado las tres evaluaciones. Si en alguna evaluación no ha  superado el 5 su nota
     * final será la menor de las tres evaluaciones:
     *
     * @return Nota final : Float
     */
    private fun evaluate(): Float {
        var finalNote = 0F
        if (binding.rbnSemipresencial.isChecked) {
            finalNote = calculateMedia(
                binding.inputEval1.text.toString().toFloat(), binding.inputEval2.text.toString()
                    .toFloat(), binding.inputEval3.text.toString().toFloat()
            )
            //
        } else if (binding.rbnPresencial.isChecked) {
            if (!checkAllApprovedNotes(
                    binding.inputEval1.text.toString().toFloat(), binding.inputEval2.text.toString()
                        .toFloat(), binding.inputEval3.text.toString().toFloat()
                )
            ) {
                finalNote = obtainMinimalNotes(
                    binding.inputEval1.text.toString().toFloat(), binding.inputEval2.text.toString()
                        .toFloat(), binding.inputEval3.text.toString().toFloat()
                )
            } else {
                finalNote = calculateMedia(
                    binding.inputEval1.text.toString().toFloat(), binding.inputEval2.text.toString()
                        .toFloat(), binding.inputEval3.text.toString().toFloat()
                )
            }
        }
        return finalNote
    }

    /**
     * Obtener la nota minima
     *
     * @param nota1 : Float
     * @param nota2: Float
     * @param nota3: Float
     * @return notaMinima: Float
     */
    private fun obtainMinimalNotes(note1: Float, note2: Float, note3: Float): Float {
        if (note1 <= note2 && note1 <= note3) {
            return note1
        }
        if (note2 < note1 && note2 <= note3) {
            return note2
        }
        return note3
    }

    /**
     * Comprueba que todas las notas estan aprovadas
     *
     * @param nota1 : Float
     * @param nota2: Float
     * @param nota3: Float
     * @return true si estan aprovadas todasﬁ: Boolean
     */
    private fun checkAllApprovedNotes(note1: Float, note2: Float, note3: Float): Boolean {
        println("checkAllApprovedNotes¡" + (note1 >= 5 && note2 >= 5 && note3 >= 5))
        return note1 >= 5 && note2 >= 5 && note3 >= 5
    }

    /**
     * Calcula la media de las notas pasadas por parámetro
     *
     * @param nota1 : Float
     * @param nota2: Float
     * @param nota3: Float
     * @return media de las notas : Float
     */
    private fun calculateMedia(note1: Float, note2: Float, note3: Float): Float {
        return (note1 + note2 + note3) / 3
    }

    /**
     * Valida que hemos introducido todas las notas con los valores necesarios
     *
     * @return si esta correcto true : Boolean
     */
    private fun validateNotes(): Boolean {
        var errorMessage = ""
        if (!noteValid(binding.inputEval1.text.toString().toFloat())) {
            errorMessage += "La nota 1 no esta en el rango válido \n"
        }
        if (!noteValid(binding.inputEval2.text.toString().toFloat())) {
            errorMessage += "La nota 2 no esta en el rango válido \n"
        }
        if (!noteValid(binding.inputEval3.text.toString().toFloat())) {
            errorMessage += "La nota 3 no esta en el rango válido \n"
        }
        if (errorMessage.isNotBlank()) {
            Toast.makeText(applicationContext, errorMessage, Toast.LENGTH_SHORT).show()
        }
        return errorMessage.isBlank()
    }

    /**
     * Validar si la nota por parámetro esta en el rango estimado de 0..10
     *
     * @return true si esta dentro del rango : Boolean
     */
    private fun noteValid(note: Float): Boolean {
        return note in 0.0..10.0
    }

    /**
     * Validar los datos de la fecha introducidos
     *
     * @return true si no hay errores en los datos introducidos
     */
    private fun validateDate(): Boolean {
        val date = Date(
            binding.inputDiaFecha.text.toString().toInt(),
            binding.inputMesFecha.text.toString().toInt(),
            binding.inputAnioFecha.text.toString().toInt()
        )
        val errorMessageDate: String = date.isCorrect()
        if (errorMessageDate.isNotBlank()) {
            Toast.makeText(
                applicationContext,
                errorMessageDate,
                Toast.LENGTH_SHORT
            ).show()
        }
        return errorMessageDate.isBlank()
    }

    /**
     * Verifica que se han introducido todos los valores de los campos
     *
     * @return true si se han introducido los valores del formulario
     */
    private fun allFieldFilled(): Boolean {
        var errorMessage = ""
        if (binding.inputNombre.text.toString().isEmpty()) {
            errorMessage += "El nombre es un dato obligatorio\n"
        }
        if (binding.inputDiaFecha.text.isEmpty()) {
            errorMessage += "El día es un dato obligatorio\n"
        }
        if (binding.inputMesFecha.text.isEmpty()) {
            errorMessage += "El mes es un dato obligatorio\n"
        }
        if (binding.inputAnioFecha.text.isEmpty()) {
            errorMessage += "El año es un dato obligatorio\n"
        }
        if (binding.inputEval1.text.isEmpty() || binding.inputEval2.text.isEmpty() || binding.inputEval3.text.isEmpty()) {
            errorMessage += "Es obligatorio el valor de todas las evaluaciones.\n"
        }
        if (errorMessage.isNotBlank()) {
            Toast.makeText(applicationContext, errorMessage, Toast.LENGTH_SHORT).show()
        }
        return errorMessage.isBlank()
    }


}