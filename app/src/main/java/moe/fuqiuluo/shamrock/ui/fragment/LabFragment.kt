package moe.fuqiuluo.shamrock.ui.fragment

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import moe.fuqiuluo.shamrock.R
import moe.fuqiuluo.shamrock.ui.app.AppRuntime
import moe.fuqiuluo.shamrock.ui.app.Level
import moe.fuqiuluo.shamrock.app.config.ShamrockConfig
import moe.fuqiuluo.shamrock.config.AliveReply
import moe.fuqiuluo.shamrock.config.AntiJvmTrace
import moe.fuqiuluo.shamrock.config.B2Mode
import moe.fuqiuluo.shamrock.config.DebugMode
import moe.fuqiuluo.shamrock.config.EnableOldBDH
import moe.fuqiuluo.shamrock.config.EnableSelfMessage
import moe.fuqiuluo.shamrock.ui.service.handlers.InitHandler
import moe.fuqiuluo.shamrock.ui.theme.GlobalColor
import moe.fuqiuluo.shamrock.ui.theme.LocalString
import moe.fuqiuluo.shamrock.ui.tools.NoticeTextDialog
import moe.fuqiuluo.shamrock.ui.tools.toast

@Composable
fun LabFragment() {
    val scope = rememberCoroutineScope()
    val ctx = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        val showNoticeDialog = remember { mutableStateOf(false) }

        NoticeBox(text = LocalString.labWarning) {
            showNoticeDialog.value = true
        }
        NoticeTextDialog(
            openDialog = showNoticeDialog,
            title = LocalString.warnTitle,
            text = LocalString.labWarning
        )

        val LocalString = LocalString
        ActionBox(
            modifier = Modifier.padding(top = 12.dp),
            painter = painterResource(id = R.drawable.baseline_preview_24),
            title = "显示设置"
        ) {
            Column {
                Divider(
                    modifier = Modifier,
                    color = GlobalColor.Divider,
                    thickness = 0.2.dp
                )

                Function(
                    title = LocalString.b2Mode,
                    desc = LocalString.b2ModeDesc,
                    descColor = it,
                    isSwitch = ShamrockConfig[ctx, B2Mode]
                ) {
                    ShamrockConfig[ctx, B2Mode] = it
                    scope.toast(ctx, LocalString.restartToast)
                    return@Function true
                }

                Function(
                    title = LocalString.showDebugLog,
                    desc = LocalString.showDebugLogDesc,
                    descColor = it,
                    isSwitch = ShamrockConfig[ctx, DebugMode]
                ) {
                    ShamrockConfig[ctx, DebugMode] = it
                    InitHandler.update(ctx)
                    return@Function true
                }
            }
        }

        ActionBox(
            modifier = Modifier.padding(top = 12.dp),
            painter = painterResource(id = R.drawable.round_logo_dev_24),
            title = "基础设置"
        ) { color ->
            Column {
                Divider(
                    modifier = Modifier,
                    color = GlobalColor.Divider,
                    thickness = 0.2.dp
                )

                Function(
                    title = "自回复测试",
                    desc = "发送[ping]，机器人发送一个具有调试信息的返回。",
                    descColor = color,
                    isSwitch = ShamrockConfig[ctx, AliveReply]
                ) {
                    ShamrockConfig[ctx, AliveReply] = it
                    return@Function true
                }

                kotlin.runCatching {
                    ctx.getSharedPreferences("shared_config", Context.MODE_WORLD_READABLE)
                }.onSuccess {
                    Function(
                        title = LocalString.persistentText,
                        desc = LocalString.persistentTextDesc,
                        descColor = color,
                        isSwitch = it.getBoolean("persistent", false)
                    ) { v ->
                        it.edit().putBoolean("persistent", v).apply()
                        scope.toast(ctx, LocalString.restartSysToast)
                        return@Function true
                    }

                    Function(
                        title = "禁用Doze模式",
                        desc = "禁止系统进入节能模式。",
                        descColor = color,
                        isSwitch = it.getBoolean("hook_doze", false)
                    ) { value ->
                        it.edit().putBoolean("hook_doze", value).apply()
                        scope.toast(ctx, LocalString.restartSysToast)
                        return@Function true
                    }
                }.onFailure {
                    AppRuntime.log("无法启用附加选项，LSPosed模块未激活或者不支持XSharedPreferences", Level.WARN)
                }
            }

        }

        ActionBox(
            modifier = Modifier.padding(top = 12.dp),
            painter = painterResource(id = R.drawable.sharp_lock_24),
            title = "安全性设置"
        ) { color ->
            Column {
                Divider(
                    modifier = Modifier,
                    color = GlobalColor.Divider,
                    thickness = 0.2.dp
                )

                Function(
                    title = LocalString.antiTrace,
                    desc = LocalString.antiTraceDesc,
                    descColor = color,
                    isSwitch = ShamrockConfig[ctx, AntiJvmTrace]
                ) {
                    ShamrockConfig[ctx, AntiJvmTrace] = it
                    scope.toast(ctx, LocalString.restartToast)
                    return@Function true
                }

                kotlin.runCatching {
                    ctx.getSharedPreferences("shared_config", Context.MODE_WORLD_READABLE)
                }.onSuccess {
                    Function(
                        title = "反检测加强",
                        desc = "可能导致某些设备频繁闪退，将拦截环境包上报。",
                        descColor = color,
                        isSwitch = it.getBoolean("super_anti", false)
                    ) { v ->
                        it.edit().putBoolean("super_anti", v).apply()
                        scope.toast(ctx, LocalString.restartToast)
                        return@Function true
                    }
                }
            }
        }

        ActionBox(
            modifier = Modifier.padding(top = 12.dp),
            painter = painterResource(id = R.drawable.round_logo_dev_24),
            title = "语音编解码器"
        ) {
            Column {
                Divider(
                    modifier = Modifier,
                    color = GlobalColor.Divider,
                    thickness = 0.2.dp
                )

                Function(
                    title = "语音流支持器",
                    desc = "请按照Wiki提示安装语音转换器。",
                    descColor = it,
                    isSwitch = AppRuntime.state.supportVoice.value
                ) {
                    if(AppRuntime.state.supportVoice.value) {
                        scope.toast(ctx, "关闭请手动删除文件。")
                    } else {
                        scope.toast(ctx, "请按照Github提示手动操作。")
                    }
                    return@Function false
                }
            }
        }

        ActionBox(
            modifier = Modifier.padding(top = 12.dp),
            painter = painterResource(id = R.drawable.round_logo_dev_24),
            title = "消息相关"
        ) {
            Column {
                Divider(
                    modifier = Modifier,
                    color = GlobalColor.Divider,
                    thickness = 0.2.dp
                )

                Function(
                    title = "自发消息推送",
                    desc = "推送Bot发送的消息，未做特殊处理请勿打开。",
                    descColor = it,
                    isSwitch = ShamrockConfig[ctx, EnableSelfMessage]
                ) {
                    ShamrockConfig[ctx, EnableSelfMessage] = it
                    InitHandler.update(ctx)
                    return@Function true
                }

                Function(
                    title = "启用旧版资源上传系统",
                    desc = "如果NT内核无法上传资源，请打开本开关。",
                    descColor = it,
                    isSwitch = ShamrockConfig[ctx, EnableOldBDH]
                ) {
                    ShamrockConfig[ctx, EnableOldBDH] = it
                    InitHandler.update(ctx)
                    return@Function true
                }
            }
        }
    }
}

@Composable
private fun Function(
    title: String,
    desc: String,
    descColor: Color,
    isSwitch: Boolean,
    onClick: (Boolean) -> Boolean
) {
    Column(modifier = Modifier
        .absolutePadding(left = 8.dp, right = 8.dp, top = 12.dp, bottom = 0.dp)
    ) {
        Text(
            modifier = Modifier.padding(2.dp),
            text = desc,
            color = descColor,
            fontSize = 11.sp
        )
        ActionSwitch(text = title, isSwitch = isSwitch) {
            onClick(it)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LabPreView() {
    LabFragment()
}