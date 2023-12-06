module.exports = {
  presets: [
    [
      '@vue/cli-plugin-babel/preset'
      // { debug: true }
    ],
    [
      '@vue/babel-preset-jsx',
      { injectH: false } // because it is bugged
    ]
  ]
}
