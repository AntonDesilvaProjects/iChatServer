package com.ichat.common;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Constants {
    public final static String SERVER_FILE_STORAGE_PATH = "C:\\ichat_files\\";
    public final static Executor THREAD_POOL = Executors.newCachedThreadPool();
}
