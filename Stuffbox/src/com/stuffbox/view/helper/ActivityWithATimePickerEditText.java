package com.stuffbox.view.helper;


public interface ActivityWithATimePickerEditText {
	
	public void showTimePickerDialog(EditTextDatePicker editTextDatePicker);
	public void setPhotoImageView(ImageViewPhoto imageView);
	public void onClickOfImageViewPhoto(String fileNameOfPhoto);
	public void showFinallyRealCapturedPhoto(String filePath);
}
