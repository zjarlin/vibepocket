package site.addzero.vibepocket.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.KoinApplication
import org.koin.core.annotation.Module

@KoinApplication
@Module
@ComponentScan("site.addzero.vibepocket.controller")
class ControllerModule
