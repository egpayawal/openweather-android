//package com.appetiser.openweather.sessionprovider
//
//import com.appetiser.auth.core.data.features.session.SessionRepository
//import com.appetiser.common.session.SessionProvider
//import javax.inject.Inject
//
//class SimpleSessionProvider @Inject constructor(
//    private val sessionRepository: SessionRepository
//) : SessionProvider {
//
//    override suspend fun getUserId(): Long =
//        sessionRepository.getSession().user.id.toLong()
//
//    override suspend fun getAccessToken(): String =
//        sessionRepository.getSession().accessToken.token
//}
