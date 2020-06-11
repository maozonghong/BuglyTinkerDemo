package com.geespace.hotfix

import android.content.Context
import dalvik.system.DexClassLoader
import dalvik.system.PathClassLoader
import java.io.File
import java.lang.reflect.Field
import java.lang.reflect.Array

/**
 * Created by maozonghong
 * on 2020/6/10
 */
object  FixDexUtils {
    private val DEX_SUFFIX = ".dex"
    private val APK_SUFFIX = ".apk"
    private val JAR_SUFFIX = ".jar"
    private val ZIP_SUFFIX = ".zip"
    private val loadedDex: HashSet<File> = HashSet()

    init {
        loadedDex.clear()

    }


    /**
     * 加载补丁
     *
     * @param context       上下文
     * @param patchFilesDir 补丁所在目录
     */
    fun loadFixedDex(context: Context?, patchFilesDir: File?) {
        if (context == null || patchFilesDir==null) {
            return
        }

        // 遍历所有的修复dex
//        val fileDir: File = patchFilesDir ?: File(context.filesDir, DEX_DIR) // data/data/包名/files/odex（这个可以任意位置）
        val listFiles= patchFilesDir.listFiles()
        if(listFiles!=null){
            for (file in listFiles) {
                if (file.name.startsWith("classes") && (file.name.endsWith(DEX_SUFFIX)
                            || file.name.endsWith(APK_SUFFIX)
                            || file.name.endsWith(JAR_SUFFIX)
                            || file.name.endsWith(ZIP_SUFFIX))
                ) {
                    loadedDex.add(file) // 存入集合
                }
            }
        }else if(patchFilesDir.name.startsWith("classes")&&patchFilesDir.name.endsWith(DEX_SUFFIX)){
            loadedDex.add(patchFilesDir)
        }

        // dex合并之前的dex
        doDexInject(context, loadedDex)
    }

    private fun doDexInject(appContext: Context, loadedDex: HashSet<File>) {
//        val optimizeDir: String =
//            appContext.filesDir.absolutePath + File.separator.toString() + OPTIMIZE_DEX_DIR // data/data/包名/files/optimize_dex（这个必须是自己程序下的目录）
//        val fopt = File(optimizeDir)
//        if (!fopt.exists()) {
//            fopt.mkdirs()
//        }
        try { // 1.加载应用程序的dex
            val pathLoader = appContext.classLoader as PathClassLoader
            for (dex in loadedDex) { // 2.加载指定的修复的dex文件
                val dexLoader = DexClassLoader(
                    dex.absolutePath,  // 修复好的dex（补丁）所在目录
                    null,  // 存放dex的解压目录（用于jar、zip、apk格式的补丁）
                    null,  // 加载dex时需要的库
                    pathLoader // 父类加载器
                )
                // 3.合并
                val dexPathList = getPathList(dexLoader)
                val pathPathList = getPathList(pathLoader)
                val leftDexElements = getDexElements(dexPathList)
                val rightDexElements = getDexElements(pathPathList)
                // 合并完成
                val dexElements = combineArray(leftDexElements, rightDexElements)
                // 重写给PathList里面的Element[] dexElements;赋值
                val pathList = getPathList(pathLoader) // 一定要重新获取，不要用pathPathList，会报错
                setField(pathList, pathList.javaClass, "dexElements", dexElements)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 反射给对象中的属性重新赋值
     */
    @Throws(NoSuchFieldException::class, IllegalAccessException::class)
    private fun setField(
        obj: Any,
        cl: Class<*>,
        field: String,
        value: Any
    ) {
        val declaredField: Field = cl.getDeclaredField(field)
        declaredField.isAccessible = true
        declaredField.set(obj, value)
    }

    /**
     * 反射得到对象中的属性值
     */
    @Throws(NoSuchFieldException::class, IllegalAccessException::class)
    private fun getField(
        obj: Any,
        cl: Class<*>,
        field: String
    ): Any {
        val localField: Field = cl.getDeclaredField(field)
        localField.isAccessible = true
        return localField.get(obj)
    }


    /**
     * 反射得到类加载器中的pathList对象
     */
    @Throws(
        ClassNotFoundException::class,
        NoSuchFieldException::class,
        IllegalAccessException::class
    )
    private fun getPathList(baseDexClassLoader: Any): Any {
        return getField(
            baseDexClassLoader,
            Class.forName("dalvik.system.BaseDexClassLoader"),
            "pathList"
        )
    }

    /**
     * 反射得到pathList中的dexElements
     */
    @Throws(NoSuchFieldException::class, IllegalAccessException::class)
    private fun getDexElements(pathList: Any): Any {
        return getField(pathList, pathList.javaClass, "dexElements")
    }

    /**
     * 数组合并
     */
    private fun combineArray(arrayLhs: Any, arrayRhs: Any): Any {
        val componentType = arrayLhs.javaClass.componentType
        val i: Int = Array.getLength(arrayLhs) // 得到左数组长度（补丁数组）
        val j: Int = Array.getLength(arrayRhs) // 得到原dex数组长度
        val k = i + j // 得到总数组长度（补丁数组+原dex数组）
        val result: Any =
            Array.newInstance(componentType, k) // 创建一个类型为componentType，长度为k的新数组
        System.arraycopy(arrayLhs, 0, result, 0, i)
        System.arraycopy(arrayRhs, 0, result, i, j)
        return result
    }

}