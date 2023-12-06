module.exports = {
  extends: ['stylelint-config-standard', 'stylelint-config-recommended-scss'],
  customSyntax: 'postcss-html',
  rules: {
    'no-descending-specificity': null,
    'scss/at-extend-no-missing-placeholder': null,
    'rule-empty-line-before': null,
    'at-rule-empty-line-before': null,
    'selector-pseudo-class-no-unknown': [true, { ignorePseudoClasses: ['deep'] }],
    'max-line-length': null,
    // 'selector-class-pattern': null,
    // for BEM compliant classes
    'selector-class-pattern': '^(?:(?:o|c|u|t|s|is|has|_|js|qa)-)?[a-zA-Z0-9]+(?:-[a-zA-Z0-9]+)*(?:__[a-zA-Z0-9]+(?:-[a-zA-Z0-9]+)*)?(?:--[a-zA-Z0-9]+(?:-[a-zA-Z0-9]+)*)?(?:\\[.+\\])?$'
  }
}
