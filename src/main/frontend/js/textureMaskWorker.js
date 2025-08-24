self.onmessage = function(e) {
  const { mainData, maskData, maskColorRgb, width, height } = e.data;
  const totalPixels = width * height;
  for (let i = 0; i < totalPixels; i++) {
    const maskR = maskData[i * 4];
    const maskG = maskData[i * 4 + 1];
    const maskB = maskData[i * 4 + 2];
    if (maskR === maskColorRgb.r && maskG === maskColorRgb.g && maskB === maskColorRgb.b) {
      mainData[i * 4] = maskColorRgb.r;
      mainData[i * 4 + 1] = maskColorRgb.g;
      mainData[i * 4 + 2] = maskColorRgb.b;
      mainData[i * 4 + 3] = maskData[i * 4 + 3];
    }
  }
  self.postMessage({ mainData });
};

