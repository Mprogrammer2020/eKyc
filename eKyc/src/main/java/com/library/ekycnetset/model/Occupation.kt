package com.library.ekycnetset.model

import java.util.ArrayList

class Occupation {

    var occupations : ArrayList<Code> = ArrayList()

    class Code {

        var id: Int ?= null
        var name: String ?= null
    }
    
}