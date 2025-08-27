function colorMatch(r1, g1, b1, r2, g2, b2, tolerance = 8) {//tolerance due to area having some noise in and around them
  return (
    Math.abs(r1 - r2) <= tolerance &&
    Math.abs(g1 - g2) <= tolerance &&
    Math.abs(b1 - b2) <= tolerance
  );
}

self.onmessage = function(e) {
  const { mainData, maskData, maskColorRgb, width, height } = e.data;
  const totalPixels = width * height;
  for (let i = 0; i < totalPixels; i++) {
    const maskR = maskData[i * 4];
    const maskG = maskData[i * 4 + 1];
    const maskB = maskData[i * 4 + 2];
    if (colorMatch(maskR, maskG, maskB, maskColorRgb.r, maskColorRgb.g, maskColorRgb.b)) {
      mainData[i * 4] = maskColorRgb.r;
      mainData[i * 4 + 1] = maskColorRgb.g;
      mainData[i * 4 + 2] = maskColorRgb.b;
      mainData[i * 4 + 3] = maskData[i * 4 + 3];
    }
  }
  self.postMessage({ mainData });
};
