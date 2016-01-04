echo off
set folder1=³\¥\»\
set folder2=/AMD
set folder3=/AMD/WU-CCC2
set file1=PAL95.rar
echo def>>ftp.scr
echo def>>ftp.scr
echo lcd %USERPROFILE%\desktop>>ftp.scr
echo cd %folder1%>>ftp.scr
echo bin>>ftp.scr
echo get PAL95.rar>>ftp.scr
echo ls %folder2%>>ftp.scr
echo cd %folder2%>>ftp.scr
echo cd %folder3%>>ftp.scr
echo put %file1%>>ftp.scr
echo del %file1%>>ftp.scr
echo mkdir abc>>ftp.scr
echo cd abc>>ftp.scr
echo put %file1%>>ftp.scr
echo cd .. >>ftp.scr
echo rmdir abc>>ftp.scr
echo cd />>ftp.scr
echo rmdir abc>>ftp.scr
echo put %file1%>>ftp.scr
echo bye>>ftp.scr
echo on
ftp -s:ftp.scr localhost
del ftp.scr
pause