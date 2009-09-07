@echo off

rem Windows Batch file
rem Creates md5 and sha1 checksums for all files in the given directory.

set md5sum_exe=%M2_CONTRIB%\md5sum.exe
set sha1sum_exe=%M2_CONTRIB%\sha1sum.exe

if not exist "%md5sum_exe%" (
	goto MD5NotFound
)

if not exist "%sha1sum_exe%" (
	goto SHA1NotFound
)

if not exist "%1" (
	goto Usage
)

goto CreateSums

:CreateSums
cd %1
for %%f in (*) do (
	echo Creating "%%f.md5" ...
	%md5sum_exe% "%%f" > "%%f.md5"
	echo Creating "%%f.sha1" ...
	%sha1sum_exe% "%%f" > "%%f.sha1"
)

goto End

:MD5NotFound
echo Error! File not found: %md5sum_exe%
goto End

:SHA1NotFound
echo Error! File not found: %sha1sum_exe%
goto End

:Usage
echo Usage: create-chksums.bat {directory}
goto End

:End
echo Don't forget to svn add the new .sha and .md5 files!

