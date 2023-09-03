import com.sun.jna.Native

object User32 {

    init {
        Native.register("user32")
    }

    @JvmStatic
    external fun GetKeyState(nVirtKey: Int): Short


}

fun isKeyPressed(keyCode: Int): Boolean = User32.GetKeyState(keyCode) < 0

fun areKeysPressed(vararg keyCodes: Int): Boolean = keyCodes.all { isKeyPressed(it) }