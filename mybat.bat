del /q bootstrap.jar
jar cvf0 bootstrap.jar -C out/production/diytomcat cn/chenyang/diytomcat/Bootstrap.class -C out/production/diytomcat cn/chenyang/diytomcat/classloader/CommonClassLoader.class
del /q lib/diytomcat.jar
cd out
cd production
cd diytomcat
jar cvf0 ../../../lib/diytomcat.jar *
cd ..
cd ..
cd ..
java -cp bootstrap.jar cn.chenyang.diytomcat.Bootstrap
pause