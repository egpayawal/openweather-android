package sampleData

import com.egpayawal.module.domain.models.CountryCode

class SampleData {

    companion object {

        const val USER_NAME = "francis.cerio1@appetiser.com.au"
        const val USER_FULL_NAME = "Johnny Sala"
        const val CORRECT_PASSWORD = "password"
        const val WRONG_PASSWORD = "123456"

        fun stubCountries(): List<CountryCode> {
            return mutableListOf<CountryCode>()
                .apply {
                    add(CountryCode(1, "PH", "63", "/images/flags/PH.png"))
                    add(CountryCode(2, "AU", "61", "/images/flags/AU.png"))
                }
        }
    }
}
