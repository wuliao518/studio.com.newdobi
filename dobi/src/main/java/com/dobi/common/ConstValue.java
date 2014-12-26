package com.dobi.common;

import android.annotation.SuppressLint;
import android.graphics.Bitmap.Config;

/**
 * 
 * @author Administrator 共同常量
 */
public class ConstValue {
	public static final String DOBI = "dobi";// 程序名Key
	public static final String NET_URL="http://www.do-bi.cn/download"; 
	@SuppressLint("SdCardPath")
	public static final String ROOT_PATH = "/dobi/";// 根目录
	public static final String CACHE_PATH = "cache/"; // 发饰文件夹
	public static final String PLAY_PATH = "play/"; // 化妆画面默认发饰文件夹
	public static final String FACE_PATH = "face/"; // 眉毛文件夹
	public static String urlPrefix="http://www.do-bi.cn/download";
	public static String JSONURL="http://www.do-bi.cn/download/newDownload.php";

	public static final String WOODEN_PATH = "wooden/"; // 古木桌系列文件夹
	public static final String TRAVE_PATH = "trave/"; // 云游四海文件夹
	public static final String MOVE_PATH = "move/"; // 移动照相馆文件夹

	public static final String SHOP = "shop/"; // 场景文件夹
	public static final String MORE_SCENE_DEFAULT = "default/";// 多人扮演场景默认

	public static final String CLOUD_PATH = "cloud/"; // 叱咤风云文件夹
	public static final String HAPPY_PATH = "happy/"; // 乐不思蜀文件夹
	public static final String FRIEND_PATH = "friend/"; // 摩登时代文件夹
	// public static final String OPERA_PATH = "opera/"; // 似水流年文件夹
	public static final String WORLD_PATH = "world/"; // 天下无双文件夹
	public static final String THREE_PATH = "three/"; // 纵横四海文件夹
	//注册与修改密码
	public static final int REGISTER_PASSWORD=0;
	public static final int UPDATE_PASSWORD=1;
	//299系列与199系列
	public static final int GOODS_199=0;
	public static final int GOODS_299=1;
	public static final int GOODS_399=2;

	public static final String MORE_CLIP_FACE = "moerClipFace/";// 多人扮演剪切脸部用

	public static final String VERSION_URL = "http://download.d-bi.cn/Version.txt";
	// public static final String VERSION_URL =
	// "http://192.168.1.11:8081/Version.txt";

	/**
	 * 截图后脸部尺寸 0: 宽，1：高. 上海截图标准为95, 140.如果宽高不合适，宽度为标准
	 */
	public static final int[] FACE_SIZE = new int[] { 400, 485 };
	/**
	 * 衣服头饰素材以脸部为该尺寸为标准 95
	 */
	public static final int FACE_BASE_WIDTH = 95;

	/**
	 * 窗体传值引用的Key
	 * 
	 * @author Administrator
	 *
	 */
	public enum ExtruaKey {
		/**
		 * 照片类型
		 */
		PhotoType,
		/**
		 * 多人扮演中，正在为哪张脸部更新照片
		 */
		MoreFaceIndex,
	}

	/**
	 * 图像类型
	 * 
	 * @author Administrator
	 *
	 */
	public enum ImgSourceType {
		/**
		 * 前置、
		 */
		front,
		/**
		 * 后置、
		 */
		back,
		/**
		 * 选择
		 */
		select
	}

	/**
	 * 需要保存到sd卡的图片名称
	 * 
	 * @author Administrator
	 *
	 */
	public enum ImgName {
		/**
		 * 拍照保存
		 */
		photo,
		/**
		 * 拍照后截图保存
		 */
		playPhotoClip, 
		facePhotoClip,
		/**
		 * 取消选择
		 */
		cancelSelect,
		/**
		 * 最终分享的图片
		 */
		resultImg,
		/**
		 * 多人分享
		 */
		moreShareImg,

	}

	/**
	 * 单人扮演装扮步骤
	 * 
	 * @author Administrator
	 *
	 */
	public enum Stage {
		/**
		 * 无步骤
		 */
		None,
		/**
		 * 化妆
		 */
		Face,
		/**
		 * 选择发饰
		 */
		Hair,
		/**
		 * 选择衣服
		 */
		Body,
		/**
		 * 选择场景
		 */
		Scene,
		/**
		 * 选择道具
		 */
		Prop
	}

	/**
	 * 新建Bitmap参数
	 */
	public final static Config MY_CONFIG_4444 = Config.ARGB_4444;
	/**
	 * 新建Bitmap参数
	 */
	public final static Config MY_CONFIG_8888 = Config.ARGB_8888;

	/**
	 * Sharepreference 的key的集合
	 * 
	 * @author Administrator
	 *
	 */
	public enum SharepreferenceKey {
		/**
		 * 前置摄像头默认旋转角度
		 */
		CameraFrontDegree,
		/**
		 * 后者摄像头默认旋转角度
		 */
		CameraBackDegree,
	}
}
