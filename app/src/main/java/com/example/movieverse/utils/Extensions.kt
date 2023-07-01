package com.example.movieverse.utils

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable


/**
 * Function to create a Flowable that emits the text changes of an EditText.
 *
 * @return A Flowable emitting the text changes of the EditText.
 */
fun EditText.textChanges(): Flowable<String> {
    return Flowable.create({ emitter ->
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                emitter.onNext(p0.toString())
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })
    }, BackpressureStrategy.LATEST)
}

/**
 * Function to hide a View by setting its visibility to INVISIBLE.
 */
fun View.hide() {
    this.visibility = View.INVISIBLE
}

/**
 * Function to show a View by setting its visibility to VISIBLE.
 */
fun View.show() {
    this.visibility = View.VISIBLE
}
