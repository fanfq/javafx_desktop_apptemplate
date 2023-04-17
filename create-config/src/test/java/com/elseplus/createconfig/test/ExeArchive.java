package com.elseplus.createconfig.test;

import com.elseplus.common.util.AppInfo;
import com.elseplus.createconfig.CreateConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SystemUtils;

import java.io.File;
import java.io.IOException;

@Slf4j
public class ExeArchive {

    public static void main(String[] args) throws IOException {
        CreateConfig.bootstrapArchive();
    }
}
