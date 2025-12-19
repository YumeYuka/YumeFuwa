package util

import kotlinx.browser.window

actual fun copyToClipboard(text: String) {
    try {
        window.navigator.clipboard.writeText(text).then(
            onFulfilled = {
                console.log("复制成功: $text")
            },
            onRejected = {
                console.error("复制失败", it)
                fallbackCopyToClipboard(text)
            }
        )
    } catch (e: Exception) {
        console.error("Clipboard API 不可用", e)
        fallbackCopyToClipboard(text)
    }
}

private fun fallbackCopyToClipboard(text: String) {
    try {
        val textarea = kotlinx.browser.document.createElement("textarea") as org.w3c.dom.HTMLTextAreaElement
        textarea.value = text
        textarea.style.position = "fixed"
        textarea.style.opacity = "0"
        kotlinx.browser.document.body?.appendChild(textarea)
        textarea.select()
        kotlinx.browser.document.execCommand("copy")
        kotlinx.browser.document.body?.removeChild(textarea)
        console.log("降级复制成功")
    } catch (e: Exception) {
        console.error("降级复制失败", e)
    }
}
