package com.crystalist.desktop.deviceconnect

import com.crystalist.shared.deviceconnect.DeviceConnectDeviceType
import com.crystalist.shared.deviceconnect.DeviceConnectManager

object DesktopDeviceConnect {
    val manager = DeviceConnectManager(localDeviceType = DeviceConnectDeviceType.DESKTOP)
}
