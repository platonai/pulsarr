package ai.platon.pulsar.examples.sites.topEc.chinese.taobao

import ai.platon.pulsar.context.PulsarContexts
import ai.platon.pulsar.crawl.DefaultPulsarEventHandler
import ai.platon.pulsar.crawl.event.LoginHandler
import ai.platon.pulsar.examples.sites.topEc.chinese.gome.GomeCrawler
import ai.platon.pulsar.examples.sites.topEc.chinese.s1688.S1688Crawler
import ai.platon.pulsar.session.PulsarSession

class TaobaoLoginHandler(
    username: String,
    password: String,
    loginUrl: String = "https://login.taobao.com",
    activateSelector: String = ".password-login-tab-item",
    usernameSelector: String = "input#fm-login-id",
    passwordSelector: String = "input#fm-login-password",
    submitSelector: String = "button[type=submit]",
    warnUpUrl: String? = null,
): LoginHandler(loginUrl,
    usernameSelector, username, passwordSelector, password,
    submitSelector, warnUpUrl, activateSelector
)

class TaobaoCrawler(
    val portalUrl: String = "https://s.taobao.com/search?spm=a21bo.jianhua.201867-main.24.5af911d9wFOWsc&q=%E6%94%B6%E7%BA%B3",
    val args: String = "-i 1s -ii 5m -ol a[href~=detail] -ignoreFailure",
    val session: PulsarSession = PulsarContexts.createSession()
) {
    // login parameters
    val username = System.getenv("PULSAR_TAOBAO_USERNAME") ?: "MustFallUsername"
    val password = System.getenv("PULSAR_TAOBAO_PASSWORD") ?: "MustFallPassword"

    fun crawl() {
        val options = session.options(args)
        val loginHandler = TaobaoLoginHandler(username, password, warnUpUrl = portalUrl)
        options.eventHandler.loadEventHandler.onAfterBrowserLaunch.addLast(loginHandler)

        session.loadOutPages(portalUrl, options)
    }
}

fun main() {
    TaobaoCrawler().crawl()
    PulsarContexts.await()
}