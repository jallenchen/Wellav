package com.wellav.dolphin.bind;

import java.io.IOException;
import java.util.Hashtable;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.wellav.dolphin.config.SysConfig;
import com.wellav.dolphin.fragment.BaseFragment;
import com.wellav.dolphin.launcher.R;
import com.wellav.dolphin.utils.Util;

public class BindGuideFragment extends BaseFragment implements OnClickListener {
	
	private static final int QR_WIDTH = 150;
	private static final int QR_HEIGHT = 150;
	private ImageView quickMarkIv;
	private ImageButton backIv;
	private Bitmap bitmap;
	private TextView nameTextView;
	private String DolphinName;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.bind_guide, container, false);
		backIv = (ImageButton) view.findViewById(R.id.actionbar_bind_prev);
		quickMarkIv = (ImageView) view
				.findViewById(R.id.bind_guide_quickmark_iv);
		backIv.setOnClickListener(this);

		nameTextView = (TextView) view.findViewById(R.id.actionbar_dolphin_num);

		Resources res = getResources();
		DolphinName = String.format(res.getString(R.string.dolphin_number),
				SysConfig.uid);
		nameTextView.setText(DolphinName);

		createImage();

		return view;
	}

	@Override
	public void onClick(View v) {
		getActivity().finish();
	}

	private void createImage() {
		try {
			QRCodeWriter writer = new QRCodeWriter();
			String textUrl = getString(R.string.dolphin_url);

			if (textUrl == null || "".equals(textUrl) || textUrl.length() < 1) {
				return;
			}

			BitMatrix martix = writer.encode(textUrl, BarcodeFormat.QR_CODE,
					QR_WIDTH, QR_HEIGHT);

			System.out.println("w:" + martix.getWidth() + "h:"
					+ martix.getHeight());

			Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
			hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
			BitMatrix bitMatrix = new QRCodeWriter().encode(textUrl,
					BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT, hints);

			int[] pixels = new int[QR_WIDTH * QR_HEIGHT];

			for (int y = 0; y < QR_HEIGHT; y++) {
				for (int x = 0; x < QR_WIDTH; x++) {
					if (bitMatrix.get(x, y)) {
						pixels[y * QR_WIDTH + x] = 0xff000000;
					} else {
						pixels[y * QR_WIDTH + x] = 0xffffffff;
					}
				}
			}

			bitmap = Bitmap.createBitmap(QR_WIDTH, QR_HEIGHT,
					Bitmap.Config.ARGB_8888);
			bitmap.setPixels(pixels, 0, QR_WIDTH, 0, 0, QR_WIDTH, QR_HEIGHT);

			System.out.println(Environment.getExternalStorageDirectory());
			quickMarkIv.setImageBitmap(bitmap);
			try {
				Util.saveMyBitmap(bitmap, "code");
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (WriterException e) {
			e.printStackTrace();
		}
	}

}
