module.exports = {
  module: {
    rules: [
      {
        test: /\.styl$/,
        use: [
          {
            loader: "style-loader", // creates style nodes from JS strings
          },
          {
            loader: "css-loader", // translates CSS into CommonJS
          },
          {
            loader: "stylus-loader", // compiles Stylus to CSS
          },
          {
            loader: 'sass-loader',
            options: {
              implementation: require('sass'), //dart-sass 적용
          },
        }
        ],
      },
    ],
  },
};
