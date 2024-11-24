
package com.zeekrlife.connect.core.manager.bluetooth.utils;

import android.text.TextUtils;
import android.util.Log;

import java.lang.reflect.Field;

/**
 * <pre>
 *     author : qiangwang
 *     e-mail : qiangwang@ecarx.com.cn
 *     time   : 2018/11/6
 *     desc   : Log tool
 *     version: 1.0
 * </pre>
 */
public final class xLog {
    /**
     * 是否在模拟器上调试
     */
    public static final boolean EMULATOR = false;

    private static final String TAG_PREFIX = "Interconnection";
    private static String TAG_APP = TAG_PREFIX;

    /**
     * 标签字段：Bluetooth
     */
    public static String SUB_TAG_BLUETOOTH = "BT_HiCar";

    /**
     * 建议在Application中初始化 TAG 标签
     *
     * @param tag 标签
     */
    public static void init(String tag) {
        if (!TextUtils.isEmpty(tag)) {
            TAG_APP = tag;
        }
    }

    private xLog() {
    }

    /**
     * 只打印函数名和行号, 实际标签是 init 方法中设置的标签，如果没有设置的话，默认标签为 "RR_APP"
     */
    public static void d() {
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        printf(TYPE_I, elements);
    }

    /**
     * 只打印函数名和行号, 实际标签是 init 方法中设置的标签，如果没有设置的话，默认标签为 "RR_APP"
     */
    public static void d(String msg) {
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        printf(TYPE_I, elements, msg);
    }

    /**
     * 注意实际标签是 "RR_APP" + "_" + TAG变量
     */
    public static void d(String TAG, String msg) {
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        printf(TYPE_I, elements, TAG, msg);
    }

    /**
     * 注意实际标签是 "RR_APP" + "_" + TAG变量
     */
    public static void d(String TAG, String msg, Throwable tr) {
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        printf(TYPE_I, elements, TAG, msg, tr);
    }

    /**
     * 只打印函数名和行号, 实际标签是 init 方法中设置的标签，如果没有设置的话，默认标签为 "RR_APP"
     */
    public static void i() {
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        printf(TYPE_I, elements);
    }

    /**
     * 只打印函数名和行号, 实际标签是 init 方法中设置的标签，如果没有设置的话，默认标签为 "RR_APP"
     */
    public static void i(String msg) {
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        printf(TYPE_I, elements, msg);
    }

    /**
     * 注意实际标签是 "RR_APP" + "_" + TAG变量
     */
    public static void i(String TAG, String msg) {
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        printf(TYPE_I, elements, TAG, msg);
    }

    /**
     * 注意实际标签是 "RR_APP" + "_" + TAG变量
     */
    public static void i(String TAG, String msg, Throwable tr) {
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        printf(TYPE_I, elements, TAG, msg, tr);
    }

    /**
     * 只打印函数名和行号, 实际标签是 init 方法中设置的标签，如果没有设置的话，默认标签为 "RR_APP"
     */
    public static void v() {
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        printf(TYPE_I, elements);
    }

    /**
     * 只打印函数名和行号, 实际标签是 init 方法中设置的标签，如果没有设置的话，默认标签为 "RR_APP"
     */
    public static void v(String msg) {
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        printf(TYPE_I, elements, msg);
    }

    /**
     * 注意实际标签是 "RR_APP" + "_" + TAG变量
     */
    public static void v(String TAG, String msg) {
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        printf(TYPE_I, elements, TAG, msg);
    }

    /**
     * 注意实际标签是 "RR_APP" + "_" + TAG变量
     */
    public static void v(String TAG, String msg, Throwable tr) {
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        printf(TYPE_I, elements, TAG, msg, tr);
    }

    /**
     * 只打印函数名和行号, 实际标签是 init 方法中设置的标签，如果没有设置的话，默认标签为 "RR_APP"
     */
    public static void w() {
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        printf(TYPE_W, elements);
    }

    /**
     * 只打印函数名和行号, 实际标签是 init 方法中设置的标签，如果没有设置的话，默认标签为 "RR_APP"
     */
    public static void w(String msg) {
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        printf(TYPE_W, elements, msg);
    }

    /**
     * 注意实际标签是 "RR_APP" + "_" + TAG变量
     */
    public static void w(String TAG, String msg) {
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        printf(TYPE_W, elements, TAG, msg);
    }

    /**
     * 注意实际标签是 "RR_APP" + "_" + TAG变量
     */
    public static void w(String TAG, String msg, Throwable tr) {
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        printf(TYPE_W, elements, TAG, msg, tr);
    }

    /**
     * 只打印函数名和行号, 实际标签是 init 方法中设置的标签，如果没有设置的话，默认标签为 "RR_APP"
     */
    public static void e() {
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        printf(TYPE_E, elements);
    }

    /**
     * 只打印函数名和行号, 实际标签是 init 方法中设置的标签，如果没有设置的话，默认标签为 "RR_APP"
     */
    public static void e(String msg) {
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        printf(TYPE_E, elements, msg);
    }

    /**
     * 注意实际标签是 "RR_APP" + "_" + TAG变量
     */
    public static void e(String TAG, String msg) {
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        printf(TYPE_E, elements, TAG, msg);
    }

    /**
     * 注意实际标签是 "RR_APP" + "_" + TAG变量
     */
    public static void e(String TAG, String msg, Throwable tr) {
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        printf(TYPE_E, elements, TAG, msg, tr);
    }

    private final static int TYPE_D = 0;
    private final static int TYPE_I = 1;
    private final static int TYPE_V = 2;
    private final static int TYPE_W = 3;
    private final static int TYPE_E = 4;

    /**
     * 只打印函数名和行号, 统一打印，保证方法统一，实际标签是 init 方法中设置的标签
     */
    private static void printf(int type, StackTraceElement[] elements) {
        if (elements == null || elements.length < 4) {
            if (type == TYPE_D) {
                Log.d(TAG_APP, "");
            } else if (type == TYPE_I) {
                Log.i(TAG_APP, "");
            } else if (type == TYPE_V) {
                Log.v(TAG_APP, "");
            } else if (type == TYPE_W) {
                Log.w(TAG_APP, "");
            } else if (type == TYPE_E) {
                Log.e(TAG_APP, "");
            }
        } else {
            if (type == TYPE_D) {
                Log.d(TAG_APP, elements[3].getFileName() + "(" + elements[3].getLineNumber() + "):"
                        + elements[3].getMethodName());
            } else if (type == TYPE_I) {
                Log.i(TAG_APP, elements[3].getFileName() + "(" + elements[3].getLineNumber() + "):"
                        + elements[3].getMethodName());
            } else if (type == TYPE_V) {
                Log.v(TAG_APP, elements[3].getFileName() + "(" + elements[3].getLineNumber() + "):"
                        + elements[3].getMethodName());
            } else if (type == TYPE_W) {
                Log.w(TAG_APP, elements[3].getFileName() + "(" + elements[3].getLineNumber() + "):"
                        + elements[3].getMethodName());
            } else if (type == TYPE_E) {
                Log.e(TAG_APP, elements[3].getFileName() + "(" + elements[3].getLineNumber() + "):"
                        + elements[3].getMethodName());
            }
        }
    }

    /**
     * 统一打印，保证方法统一，实际标签是 init 方法中设置的标签
     */
    private static void printf(int type, StackTraceElement[] elements, String msg) {
        if (elements == null || elements.length < 4) {
            if (type == TYPE_D) {
                Log.d(TAG_APP, msg);
            } else if (type == TYPE_I) {
                Log.i(TAG_APP, msg);
            } else if (type == TYPE_V) {
                Log.v(TAG_APP, msg);
            } else if (type == TYPE_W) {
                Log.w(TAG_APP, msg);
            } else if (type == TYPE_E) {
                Log.e(TAG_APP, msg);
            }
        } else {
            if (type == TYPE_D) {
                Log.d(TAG_APP, elements[3].getFileName() + "(" + elements[3].getLineNumber() + "):"
                        + elements[3].getMethodName() + ": " + msg);
            } else if (type == TYPE_I) {
                Log.i(TAG_APP, elements[3].getFileName() + "(" + elements[3].getLineNumber() + "):"
                        + elements[3].getMethodName() + ": " + msg);
            } else if (type == TYPE_V) {
                Log.v(TAG_APP, elements[3].getFileName() + "(" + elements[3].getLineNumber() + "):"
                        + elements[3].getMethodName() + ": " + msg);
            } else if (type == TYPE_W) {
                Log.w(TAG_APP, elements[3].getFileName() + "(" + elements[3].getLineNumber() + "):"
                        + elements[3].getMethodName() + ": " + msg);
            } else if (type == TYPE_E) {
                Log.e(TAG_APP, elements[3].getFileName() + "(" + elements[3].getLineNumber() + "):"
                        + elements[3].getMethodName() + ": " + msg);
            }
        }
    }

    /**
     * 统一打印，保证方法统一，注意实际标签是 "RR_APP" + "_" + TAG变量
     */
    private static void printf(int type, StackTraceElement[] elements, String TAG, String msg) {
        if (elements == null || elements.length < 4) {
            if (type == TYPE_D) {
                Log.d(TAG_PREFIX + "_" + TAG, msg);
            } else if (type == TYPE_I) {
                Log.i(TAG_PREFIX + "_" + TAG, msg);
            } else if (type == TYPE_V) {
                Log.v(TAG_PREFIX + "_" + TAG, msg);
            } else if (type == TYPE_W) {
                Log.w(TAG_PREFIX + "_" + TAG, msg);
            } else if (type == TYPE_E) {
                Log.e(TAG_PREFIX + "_" + TAG, msg);
            }
        } else {
            if (TextUtils.isEmpty(TAG)) {
                TAG = elements[3].getClassName();
            }
            if (type == TYPE_D) {
                Log.d(TAG_PREFIX + "_" + TAG, elements[3].getFileName()
                        + "(" + elements[3].getLineNumber() + "):" + elements[3].getMethodName()
                        + ": " + msg);
            } else if (type == TYPE_I) {
                Log.i(TAG_PREFIX + "_" + TAG, elements[3].getFileName()
                        + "(" + elements[3].getLineNumber() + "):" + elements[3].getMethodName()
                        + ": " + msg);
            } else if (type == TYPE_V) {
                Log.v(TAG_PREFIX + "_" + TAG, elements[3].getFileName()
                        + "(" + elements[3].getLineNumber() + "):" + elements[3].getMethodName()
                        + ": " + msg);
            } else if (type == TYPE_W) {
                Log.w(TAG_PREFIX + "_" + TAG, elements[3].getFileName()
                        + "(" + elements[3].getLineNumber() + "):" + elements[3].getMethodName()
                        + ": " + msg);
            } else if (type == TYPE_E) {
                Log.e(TAG_PREFIX + "_" + TAG, elements[3].getFileName()
                        + "(" + elements[3].getLineNumber() + "):" + elements[3].getMethodName()
                        + ": " + msg);
            }
        }
    }

    /**
     * 统一打印，保证方法统一，注意实际标签是 "RR_APP" + "_" + TAG变量
     */
    private static void printf(int type, StackTraceElement[] elements, String TAG, String msg, Throwable tr) {
        if (elements == null || elements.length < 4) {
            if (type == TYPE_D) {
                Log.d(TAG_PREFIX + "_" + TAG, msg);
            } else if (type == TYPE_I) {
                Log.i(TAG_PREFIX + "_" + TAG, msg);
            } else if (type == TYPE_V) {
                Log.v(TAG_PREFIX + "_" + TAG, msg);
            } else if (type == TYPE_W) {
                Log.w(TAG_PREFIX + "_" + TAG, msg);
            } else if (type == TYPE_E) {
                Log.e(TAG_PREFIX + "_" + TAG, msg);
            }
        } else {
            if (TextUtils.isEmpty(TAG)) {
                TAG = elements[3].getClassName();
            }
            if (type == TYPE_D) {
                Log.d(TAG_PREFIX + "_" + TAG, elements[3].getFileName()
                        + "(" + elements[3].getLineNumber() + "):" + elements[3].getMethodName()
                        + ": " + msg, tr);
            } else if (type == TYPE_I) {
                Log.i(TAG_PREFIX + "_" + TAG, elements[3].getFileName()
                        + "(" + elements[3].getLineNumber() + "):" + elements[3].getMethodName()
                        + ": " + msg, tr);
            } else if (type == TYPE_V) {
                Log.v(TAG_PREFIX + "_" + TAG, elements[3].getFileName()
                        + "(" + elements[3].getLineNumber() + "):" + elements[3].getMethodName()
                        + ": " + msg, tr);
            } else if (type == TYPE_W) {
                Log.w(TAG_PREFIX + "_" + TAG, elements[3].getFileName()
                        + "(" + elements[3].getLineNumber() + "):" + elements[3].getMethodName()
                        + ": " + msg, tr);
            } else if (type == TYPE_E) {
                Log.e(TAG_PREFIX + "_" + TAG, elements[3].getFileName()
                        + "(" + elements[3].getLineNumber() + "):" + elements[3].getMethodName()
                        + ": " + msg, tr);
            }
        }
    }

    /**
     * 对象是否为空字符串
     *
     * @param obj 对象
     * @return 字符串
     */
    public static String toNull(Object obj) {
        return ((obj == null) ? "==null" : "!=null");
    }

    /**
     * 打印出 class 里面定义的常量名
     * 例： class 里面定义了 public static final int ID_MUSIC = 0;
     * 如果 id 传 0， 则返回 "ID_MUSIC"
     *
     * @param id 需要打印的id
     * @param c  类对象，一般传 类.class
     * @return
     */
    public static String getName(int id, Class c) {
        return getName(id, c, "unknown:" + id);
    }

    /**
     * 打印出 class 里面定义的常量名
     *
     * @param id
     * @param c
     * @param unknownString 未定义的提示
     * @return
     */
    public static String getName(int id, Class c, String unknownString, String... exceptArray) {
        if (c == null) {
            return unknownString;
        }
        Field[] fields = c.getDeclaredFields();
        if (fields == null) {
            return unknownString;
        }
        try {
            for (Field field : fields) {
                if (field == null) {
                    continue;
                }
                field.setAccessible(true);
                if (field.getType() != int.class) {
                    continue;
                }
                int value = field.getInt(null);
                if (value != id) {
                    continue;
                }
                final String string = field.getName();
                boolean find = true;
                if (null != exceptArray) {
                    for (int i = 0; i < exceptArray.length; i++) {
                        if (TextUtils.equals(string, exceptArray[i])) {
                            find = false;
                            break;
                        }
                    }
                }
                if (find) {
                    return string;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return unknownString;
    }
}