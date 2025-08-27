import pluginVue from "eslint-plugin-vue"
import tseslint from "typescript-eslint"
import skipFormatting from "@vue/eslint-config-prettier/skip-formatting"

export default [
  // TS 파일 (타입 검사 포함)
  {
    files: ["**/*.{ts,mts,tsx}"],
    languageOptions: {
      parser: tseslint.parser,
      parserOptions: {
        project: "./tsconfig.app.json",
      },
    },
    plugins: {
      "@typescript-eslint": tseslint.plugin,
    },
  },

  // Vue 파일 (문법 검사만)
  {
    files: ["**/*.vue"],
    languageOptions: {
      parser: require("vue-eslint-parser"),
      parserOptions: {
        parser: "@typescript-eslint/parser",
        extraFileExtensions: [".vue"],
        project: null, // vue 파일은 타입 검사 제외
      },
    },
    plugins: {
      vue: pluginVue,
    },
  },
  {
    languageOptions: {
      parserOptions: {
        project: ['./tsconfig.json'],
      },
    },
  },

  globalIgnores(['**/dist/**', '**/dist-ssr/**', '**/coverage/**']),

  // Vue 권장 규칙
  ...pluginVue.configs["flat/recommended"],

  // TS 권장 규칙
  ...tseslint.configs.recommended,

  // prettier 충돌 방지
  skipFormatting,
]
