package au.com.skater901.wc3connect

import au.com.skater901.wc3connect.core.service.GameNotifier
import au.com.skater901.wc3connect.core.service.NotificationService
import com.google.inject.AbstractModule
import com.google.inject.Injector
import kotlin.reflect.KClass

public interface NotificationModule<T : Any, C : Any> {
    /**
     * The unique name of this module. Recommendation is for this to be a single word with no special characters.
     */
    public val moduleName: String

    /**
     * The main instance of the library used for this module, assuming you have one. For example, the Discord module
     * uses the JDA library, which has a JDA interface that acts as the main class to interact with Discord. An instance
     * of this class will be provided to the [initializeNotificationHandlers] method.
     */
    public val mainSystemClass: KClass<T>

    /**
     * A simple class with a primary constructor listing all the configuration properties you need. The configuration
     * properties in the config file at runtime need to be namespaced with your [moduleName].
     *
     * For example, if you had a Facebook Module, and your [moduleName] was "facebook", the config properties
     * in the config file would be prefixed with "facebook.". Assume that you need a username and password, your config
     * class would look like this:
     * ```kotlin
     * class FacebookConfig(
     *     val username: String,
     *     val password: String
     * )
     * ```
     * Then, in the config file, you would have the following properties:
     * ```text
     * facebook.username=myUsername
     * facebook.password=myPassword
     * ```
     * Currently, there a limited number of types supported for config. The supported types can be seen in [au.com.skater901.wc3connect.application.config.ConfigParser.get].
     * New types can be added on request.
     *
     * An instance of your [configClass], populated with configuration values, will be provided to the [initializeNotificationHandlers] method.
     */
    public val configClass: KClass<C>

    /**
     * Provide a Guice module where all injection configurations required for your module are configured. This is
     * technically optional, hence the default value being an empty [AbstractModule]. However, if you don't provide
     * a configuration binding for your [mainSystemClass], the injection will fail when calling [initializeNotificationHandlers].
     *
     * This may need to be rectified... make main class nullable so it's not always injected? idk
     *
     * If you're unfamiliar with Guice, here's an easy way to provide your main class:
     * ```kotlin
     * package my.package
     *
     * import com.facebook.FacebookBuilder
     * import com.facebook.FacebookInterface
     * import com.google.inject.AbstractModule
     * import com.google.inject.Provides
     * import jakarta.inject.Inject
     * import jakarta.inject.Singleton
     * import my.package.FacebookConfiguration
     *
     * FacebookGuiceModule : AbstractModule() {
     *     @Provides
     *     @Inject
     *     @Singleton // This can be omitted if your main class can be instantiated multiple times, but 99% of the time that won't be the case
     *     fun provideFacebook(config: FacebookConfiguration): FacebookInterface {
     *         return FacebookBuilder().withUsername(config.username)
     *             .withPassword(config.password)
     *             .build()
     *     }
     * }
     * ```
     */
    public fun guiceModule(): AbstractModule = object : AbstractModule() {}

    /**
     * Function to start whatever is needed for your API. This is the place to instantiate any classes required for
     * receiving interactions via your protocol. At a minimum, you should be able to receive requests to add a new
     * notification listener, otherwise your module is useless.
     *
     * @param mainClass An instance of your [mainSystemClass]. Generally this will be where you need to register your
     * API listeners.
     * @param config An instance of your [configClass] populated with the configuration for this module.
     * @param injector A Guice [Injector] that can be used for instantiating your listeners via injection. As a quick
     * example, if you had a class called `FacebookRegisterNotification`, you would get an instance by going `injector.getInstance(FacebookRegisterNotification::class.java)`
     * @param notificationService An instance of the [NotificationService]. This is provided in case you prefer to
     * construct your classes directly, rather than using Guice's dependency injection method.
     */
    public fun initializeNotificationHandlers(
        mainClass: T,
        config: C,
        injector: Injector,
        notificationService: NotificationService
    )

    /**
     * An optional scheduled task that you want to be run. Can be used for registering code that polls for something.
     */
    public val scheduledTask: ScheduledTask?
        get() = null

    /**
     * The class you have created that implements [GameNotifier], and is used for notifying on new games, updating
     * existing games that get modified, and closing off games that have started or been unhosted.
     */
    public val gameNotifier: KClass<out GameNotifier>
}