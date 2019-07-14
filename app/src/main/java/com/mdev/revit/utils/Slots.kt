package com.mdev.revit.utils

import android.util.SparseArray

class Slots{

    companion object{
        fun getSlots(): SparseArray<List<String>> {
            val refSlots = SparseArray<List<String>>()
            refSlots.append(
                0, listOf(
                    "A1/L1", "F1/L2", "D1/L3", "TB1/L4", "TG1/L5", "/L6",
                    "A2/L31", "F2/L32", "D2/L33", "TB2/L34", "TG2/L35", "/L36",
                    "H1/L61", "K1/L62"
                )
            )
            refSlots.append(
                1, listOf(
                    "B1/L7", "G1/L8", "E1/L9", "TC1/L10", "TAA1/L11", "/L12",
                    "B2/L37", "G2/L38", "E2/L39", "TC2/L40", "TAA2/L41", "/L42",
                    "H2/L63", "K2/L64"
                )
            )
            refSlots.append(
                2, listOf(
                    "C1/L13", "A1/L14", "F1/L15", "TD1/L16", "/L17", "/L18",
                    "C2/L43", "A2/L44", "F2/L45", "TD2/L46", "TBB2/L47", "/L48",
                    "H3/L65", "K3/L66"
                )
            )
            refSlots.append(
                3, listOf(
                    "D1/L19", "B1/L20", "G1/L21", "TE1/L22", "TCC1/L23", "/L24",
                    "D2/L49", "B2/L50", "G2/L51", "TE2/L52", "TCC2/L53", "/L54",
                    "H4/L67", "K4/L68"
                )
            )
            refSlots.append(
                4, listOf(
                    "E1/L25", "C1/L26", "TA1/L27", "TF1/L28", "TDD1/L29", "/L30",
                    "E2/L55", "C2/L56", "TA2/L57", "TF2/L58", "TDD2/L59", "/L60",
                    "H5/L69", "K5/L70"
                )
            )
            refSlots.append(5, listOf())
            refSlots.append(6, listOf())

            return refSlots
        }

        fun getTime(): List<List<String>>{

            return listOf(listOf(
                "08:00 - 08:50","08:55 - 09:45","09:50 - 10:40","10:45 - 11:35","11:40 - 12:30","",
                "14:00 - 14:50","14:55 - 15:45","15:50 - 16:40","16:45 - 17:35","17:40 - 18:30","",
                "19:00 - 19:50","20:00 - 20:50"
            )
                , listOf(
                    "08:00 - 08:50","08:50 - 09:40","09:50 - 10:40","10:40 - 11:30","11:40 - 12:30","12:30 - 13:20",
                    "14:00 - 14:50","14:50 - 15:40","15:50 - 16:40","16:40 - 17:30","17:40 - 18:30","18:30 - 19:20",
                    "19:30 - 20:20","20:20 - 21:10"
                ))
        }
    }
}