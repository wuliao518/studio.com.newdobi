package com.dobi.logic;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import android.os.Environment;

import com.dobi.common.ConstValue;

public class Intelegence {
	/**
	 * 检查并创建图片存放目录创建目录
	 */
	public void CheckAndCreatRoot() {
		String root = Environment.getExternalStorageDirectory()
				+ ConstValue.ROOT_PATH;
		File fileFolder = new File(root);
		if (!fileFolder.exists()) {
			fileFolder.mkdir();
		}

		fileFolder = new File(root + ConstValue.PLAY_PATH);
		if (!fileFolder.exists()) {
			fileFolder.mkdir();

		}

		fileFolder = new File(root + ConstValue.FACE_PATH);
		if (!fileFolder.exists()) {
			fileFolder.mkdir();
		}

		fileFolder = new File(root + ConstValue.CACHE_PATH);
		if (!fileFolder.exists()) {
			fileFolder.mkdir();
		}


		fileFolder = new File(root + ConstValue.SHOP);
		if (!fileFolder.exists()) {
			fileFolder.mkdir();
		}
		fileFolder = new File(root + ConstValue.MORE_CLIP_FACE);
		if (!fileFolder.exists()) {
			fileFolder.mkdir();
		}

		fileExists();

	}

	// 判断文件是否存在
	private void fileExists() {
		BufferedWriter fileWriter = null;
		File versionFile = new File(Environment.getExternalStorageDirectory()
				+ ConstValue.ROOT_PATH + "Version.txt");
		if (!versionFile.exists()) {
			try {
				versionFile.createNewFile();
				fileWriter = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(versionFile, true), "UTF-8"));
				fileWriter.append("0");
				fileWriter.flush();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (fileWriter != null) {
					try {
						fileWriter.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}
