/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.adonis.Nukepad;

import java.io.*;
import java.net.*;
import java.util.*;

class PackageRegistry {
    private static final Map<String, List<String>> STDLIB = new HashMap<>();
    static {
        STDLIB.put("java", Arrays.asList(
            "java.util","java.io","java.nio","java.net","java.awt","java.swing",
            "javax.swing","java.lang","java.math","java.time","java.sql",
            "java.security","java.text","java.beans","java.reflect"
        ));
        STDLIB.put("python", Arrays.asList(
            "os","sys","re","math","json","time","datetime","random","pathlib",
            "collections","itertools","functools","threading","subprocess",
            "socket","http","urllib","logging","unittest","typing","abc",
            "io","hashlib","base64","csv","xml","html","enum","dataclasses"
        ));
        STDLIB.put("js", Arrays.asList(
            "fs","path","http","https","os","crypto","stream","events",
            "util","assert","buffer","child_process","cluster","dns",
            "net","readline","timers","url","zlib","v8","vm","worker_threads"
        ));
        STDLIB.put("ts", STDLIB.get("js"));
        STDLIB.put("tsx", STDLIB.get("js"));
        STDLIB.put("jsx", STDLIB.get("js"));
        STDLIB.put("cpp", Arrays.asList(
            "iostream","fstream","sstream","string","vector","map","set",
            "unordered_map","unordered_set","algorithm","numeric","memory",
            "thread","mutex","chrono","filesystem","regex","tuple","array",
            "stack","queue","deque","list","functional","utility","cassert",
            "cmath","cstdio","cstdlib","cstring","ctime","climits"
        ));
        STDLIB.put("c", Arrays.asList(
            "stdio.h","stdlib.h","string.h","math.h","time.h","ctype.h",
            "assert.h","errno.h","float.h","limits.h","locale.h","setjmp.h",
            "signal.h","stdarg.h","stddef.h","stdint.h","inttypes.h","stdbool.h"
        ));
        STDLIB.put("go", Arrays.asList(
            "fmt","os","io","net","http","strings","strconv","math","sort",
            "sync","time","errors","log","bufio","bytes","encoding","reflect",
            "regexp","path","filepath","runtime","context","flag","testing"
        ));
        STDLIB.put("rs", Arrays.asList(
            "std::io","std::fs","std::collections","std::fmt","std::env",
            "std::path","std::process","std::sync","std::thread","std::time",
            "std::net","std::error","std::ops","std::iter","std::str",
            "std::string","std::vec","std::rc","std::cell","std::mem"
        ));
        STDLIB.put("php", Arrays.asList(
            "array","string","math","date","file","json","pdo","mysqli",
            "curl","session","cookie","header","preg","str","is","in_array"
        ));
        STDLIB.put("cs", Arrays.asList(
            "System","System.IO","System.Net","System.Text","System.Linq",
            "System.Collections","System.Threading","System.Reflection",
            "System.Diagnostics","System.Runtime","System.Security",
            "Microsoft.Extensions","System.ComponentModel","System.Data"
        ));
    }
    
    private static final Map<String, List<String>> KNOWN_THIRD_PARTY = new HashMap<>();
    static {
        KNOWN_THIRD_PARTY.put("python", Arrays.asList(
            "numpy","pandas","matplotlib","scipy","sklearn","tensorflow",
            "torch","flask","django","fastapi","requests","sqlalchemy",
            "pytest","pydantic","celery","redis","PIL","cv2","boto3",
            "paramiko","cryptography","aiohttp","httpx","click","rich"
        ));
        KNOWN_THIRD_PARTY.put("js", Arrays.asList(
            "react","react-dom","vue","angular","express","lodash","axios",
            "moment","dayjs","webpack","vite","jest","mocha","chai",
            "typescript","next","nuxt","gatsby","electron","socket.io",
            "mongoose","sequelize","prisma","graphql","apollo","redux",
            "zustand","tailwindcss","styled-components","framer-motion"
        ));
        KNOWN_THIRD_PARTY.put("ts",  KNOWN_THIRD_PARTY.get("js"));
        KNOWN_THIRD_PARTY.put("tsx", KNOWN_THIRD_PARTY.get("js"));
        KNOWN_THIRD_PARTY.put("jsx", KNOWN_THIRD_PARTY.get("js"));
        KNOWN_THIRD_PARTY.put("java", Arrays.asList(
            "org.springframework","com.google.guava","org.apache.commons",
            "com.fasterxml.jackson","org.junit","org.mockito","io.netty",
            "org.hibernate","com.squareup.okhttp3","org.slf4j","ch.qos.logback",
            "org.projectlombok","io.reactivex","org.jetbrains.annotations"
        ));
        KNOWN_THIRD_PARTY.put("rs", Arrays.asList(
            "serde","tokio","reqwest","clap","anyhow","thiserror","log",
            "env_logger","chrono","uuid","rand","regex","lazy_static",
            "rayon","crossbeam","actix-web","rocket","diesel","sqlx"
        ));
        KNOWN_THIRD_PARTY.put("go", Arrays.asList(
            "github.com/gin-gonic/gin","github.com/gorilla/mux",
            "github.com/stretchr/testify","go.uber.org/zap",
            "github.com/spf13/cobra","github.com/go-gorm/gorm",
            "github.com/sirupsen/logrus","github.com/pkg/errors"
        )); 
    }
    public static List<String> getKnown(String ext) {
        List<String> result = new ArrayList<>();
        result.addAll(STDLIB.getOrDefault(ext, Collections.emptyList()));
        result.addAll(KNOWN_THIRD_PARTY.getOrDefault(ext, Collections.emptyList()));
        return result;
    }
    
    public static boolean isKnown(String ext, String importToken) {
        for(String pkg : getKnown(ext)) {
            if (importToken.startsWith(pkg) || pkg.startsWith(importToken))
                return true;
        }
        return false;
    }
    //live registry lookup
    public static List<String> queryRegistry(String ext, String prefix) {
        try {
            switch (ext) {
                case "python": return queryPyPI(prefix);
                case "js":
                case "ts":
                case "tsx":
                case"jsx": return queryNpm(prefix);
                default: return Collections.emptyList();
            }
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
    private static List <String> queryPyPI(String prefix) throws Exception {
        String url = "https://pypi.org/simple/";
        String searchUrl = "https://pypi.org/pypi/" + URLEncoder.encode(prefix, "UTF-8") + "/json";
        HttpURLConnection conn = (HttpURLConnection) new URL(searchUrl).openConnection();
        conn.setConnectTimeout(3000);
        conn.setReadTimeout(3000);
        conn.setRequestProperty("Accept", "application/json");
        if (conn.getResponseCode() == 200) {
            return Collections.singletonList(prefix);
        }
        return Collections.emptyList();
    }
    private static List<String> queryNpm(String prefix) throws Exception { 
        String searchUrl = "https://registry.npmjs.org/-/v1/search?text="
                + URLEncoder.encode(prefix, "UTF-8") + "&size=8";
        HttpURLConnection conn = (HttpURLConnection) new URL(searchUrl).openConnection();
        conn.setConnectTimeout(3000);
        conn.setReadTimeout(3000);
        conn.setRequestProperty("Accept", "application/json");
        if (conn.getResponseCode() != 200) return Collections.emptyList();

        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) sb.append(line);
        br.close();
        
        List<String> names = new ArrayList<>();
        String json = sb.toString();
        int idx = 0;
        while ((idx = json.indexOf("\"name\"", idx)) != -1) {
            int colon = json.indexOf(':', idx);
            int q1    = json.indexOf('"', colon + 1);
            int q2    = json.indexOf('"', q1 + 1);
            if (q1 != -1 && q2 != -1) {
                names.add(json.substring(q1 + 1, q2));
            }
            idx = q2 + 1;
        }
        return names;
    }
    
}
