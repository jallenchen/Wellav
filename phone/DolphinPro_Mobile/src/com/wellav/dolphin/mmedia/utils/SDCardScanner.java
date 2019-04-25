package com.wellav.dolphin.mmedia.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.StatFs;

import com.wellav.dolphin.application.DolphinApp;

public class SDCardScanner {
	/*
	 * avoid initializations of tool classes
	 */
	private SDCardScanner() {
	}

	/**
	 * @Title: getExtSDCardPaths
	 * @Description: to obtain storage paths, the first path is theoretically
	 *               the returned value of
	 *               Environment.getExternalStorageDirectory(), namely the
	 *               primary external storage. It can be the storage of internal
	 *               device, or that of external sdcard. If paths.size() >1,
	 *               basically, the current device contains two type of storage:
	 *               one is the storage of the device itself, one is that of
	 *               external sdcard. Additionally, the paths is directory.
	 * @return List<String>
	 * @throws IOException
	 */
	/**
	 * 获取手机所有的SDCard
	 * @return
	 */
	public static List<String> getExtSDCardPaths() {
		List<String> paths = new ArrayList<String>();
		String extFileStatus = Environment.getExternalStorageState();
		File extFile = Environment.getExternalStorageDirectory();
		if (extFileStatus.endsWith(Environment.MEDIA_MOUNTED)
				&& extFile.exists() && extFile.isDirectory()
				&& extFile.canWrite()) {
			paths.add(extFile.getAbsolutePath());
		}
		try {
			// obtain executed result of command line code of 'mount', to judge
			// whether tfCard exists by the result
			Runtime runtime = Runtime.getRuntime();
			Process process = runtime.exec("mount");
			InputStream is = process.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			int mountPathIndex = 1;
			while ((line = br.readLine()) != null) {
				// format of sdcard file system: vfat/fuse
				if ((!line.contains("fat") && !line.contains("fuse") && !line
						.contains("storage"))
						|| line.contains("secure")
						|| line.contains("asec")
						|| line.contains("firmware")
						|| line.contains("shell")
						|| line.contains("obb")
						|| line.contains("legacy") || line.contains("data")) {
					continue;
				}
				String[] parts = line.split(" ");
				int length = parts.length;
				if (mountPathIndex >= length) {
					continue;
				}
				String mountPath = parts[mountPathIndex];
				if (!mountPath.contains("/") || mountPath.contains("data")
						|| mountPath.contains("Data")) {
					continue;
				}
				File mountRoot = new File(mountPath);
				if (!mountRoot.exists() || !mountRoot.isDirectory()
						|| !mountRoot.canWrite()) {
					continue;
				}
				boolean equalsToPrimarySD = mountPath.equals(extFile
						.getAbsolutePath());
				if (equalsToPrimarySD) {
					continue;
				}
				paths.add(mountPath);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return paths;
	}


	/**
	 * @param path第一季目录所在路径
	 * @param dirNam1第一级目录
	 * @param dirNam2第二级目录
	 * @return第二季路径
	 */
	public static String mkdir(String path, String dirNam1, String dirNam2) {
		File fileObj = new File(path + "/" + dirNam1 + "/" + dirNam2);
		if (!fileObj.exists() || !fileObj.isDirectory()) {
			fileObj.mkdirs();
		}
		return fileObj.getPath();
	}

	// transform bitmap into jpg and store it
	/**
	 * 将mBitmap保存至savedFilePath
	 * @param mBitmap
	 * @param savedFilePath
	 * @return
	 */
	public static boolean saveMyBitmap(Bitmap mBitmap, String savedFilePath) {
		File f = new File(savedFilePath);
		FileOutputStream fOut = null;
		try {
			fOut = new FileOutputStream(f);
			mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);

			fOut.flush();

			fOut.close();
			mBitmap.recycle();
			return true;
		} catch (Exception e) {
			mBitmap.recycle();
			DolphinApp.MyLoge("", "" + e.getMessage());
			return false;
		}
	}

	
	/**
	 * @param path
	 * @return
	 */
	@SuppressLint("NewApi") public static long getSDTotalSize(File path) {
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSizeLong() / 1024;
		long totalBlocks = stat.getBlockCountLong() / 1024;
		return blockSize * totalBlocks;
	}

	/**
	 *  File file = new File(sdcard); StatFs statFs =
	 * new StatFs(file.getPath()); int availableSpare = ;
	 * 
	 * @return
	 */
	public static int getSDAvailableSize(File path) {
		StatFs stat = new StatFs(path.getPath());
		int blockSize = stat.getBlockSize() / 1024;
		int availableBlocks = (stat.getAvailableBlocks() - 4) / 1024;
		return blockSize * availableBlocks;
	}

	/**
	 * ��û����ڴ��ܴ�С
	 * 
	 * @return
	 */
	private static int getRomTotalSize() {
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		int blockSize = stat.getBlockSize() / 1024;
		int totalBlocks = stat.getBlockCount() / 1024;
		return blockSize * totalBlocks;
	}

	/**
	 * ��û�������ڴ�
	 * 
	 * @return
	 */
	private static int getRomAvailableSize() {
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		int blockSize = stat.getBlockSize() / 1024;
		int availableBlocks = stat.getAvailableBlocks() / 1024;
		return blockSize * availableBlocks;
	}

	public static String getStorePath() {
		List<String> paths = SDCardScanner.getExtSDCardPaths();
		File f;
		if (paths.size() == 1) {
			f = new File(paths.get(0));
			if (SDCardScanner.getSDAvailableSize(f) < 1) {

				return null;
			}
		} else if (paths.size() == 2) {
			f = new File(paths.get(1));
			if (SDCardScanner.getSDAvailableSize(f) < 1) {
				f = new File(paths.get(0));
				if (SDCardScanner.getSDAvailableSize(f) < 1) {

					return null;
				}
			}
		} else {

			return null;
		}
		return f.getPath();
	}

}
