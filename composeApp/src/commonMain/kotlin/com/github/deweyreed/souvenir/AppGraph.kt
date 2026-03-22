package com.github.deweyreed.souvenir

import androidx.lifecycle.ViewModel
import com.github.deweyreed.souvenir.base.api.Qualifiers
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.Provider
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactory
import dev.zacsweers.metrox.viewmodel.MetroViewModelFactory
import dev.zacsweers.metrox.viewmodel.ViewModelAssistedFactory
import dev.zacsweers.metrox.viewmodel.ViewModelGraph
import kotlin.reflect.KClass

@DependencyGraph(AppScope::class)
interface AppGraph : ViewModelGraph {
    @Provides
    @Qualifiers.AppId
    fun getAppId(): String = "com.github.deweyreed.souvenir"
}

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
@Inject
class AppViewModelFactory(
    override val viewModelProviders: Map<KClass<out ViewModel>, Provider<ViewModel>>,
    override val assistedFactoryProviders: Map<KClass<out ViewModel>, Provider<ViewModelAssistedFactory>>,
    override val manualAssistedFactoryProviders: Map<KClass<out ManualViewModelAssistedFactory>,
        Provider<ManualViewModelAssistedFactory>>,
) : MetroViewModelFactory()
