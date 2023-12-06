<template>
  <component
    :is="LayoutComponent"
    v-if="LayoutComponent"
  >
    <router-view />
  </component>
  <router-view v-else />
</template>

<script>
export default {
  provide () {
    return {
      navigation: this.navigation
    }
  },
  data () {
    return {
      layout: undefined,
      navigation: {
        // must be nested to preserve reactivity
        previous: undefined
      }
    }
  },
  computed: {
    LayoutComponent () {
      if (this.layout) {
        return () => import('@/layouts/' + this.layout)
      } else {
        return undefined
      }
    }
  },
  watch: {
    $route: {
      immediate: true,
      handler (to, from) {
        this.navigation.previous = (from?.name ?? null) != null ? from : undefined

        const metas = to.matched.filter(r => r.meta.layout).map(r => r.meta.layout)
        const layout = metas.length > 0 ? metas[metas.length - 1] : undefined
        if (layout !== this.layout) {
          this.layout = layout
        }
      }
    }
  }
}
</script>

<style lang="scss">
@import "@/assets/scss/resets";
@import "@/assets/scss/helpers";
@import "@/assets/scss/utilities";
@import "@/assets/scss/icons";
@import "@/assets/scss/typography";
@import "@/assets/scss/lists";
@import "@/assets/scss/cards";
@import "@/assets/scss/tables";
@import "@/assets/scss/buttons";
@import "@/assets/scss/labels";
@import "@/assets/scss/loading";
@import "@/assets/scss/avatars";
@import "@/assets/scss/empty-states";
//global component styles
@import "@/assets/scss/tabs";
@import "@/assets/scss/forms";
//global layout styles
@import "@/assets/scss/grid";
@import "@/assets/scss/layouts/layout-sidebar";
@import "@/assets/scss/layouts/layout-search-results";
@import "@/assets/scss/layouts/layout-wizard";

:root {
  --dark: #2e3242;
  --slate: #45555f;
  --gray: #717782;
  --gray400: #8f95a1;
  --blue: #009de0;
  --light-blue: #eef2f5;
  --magenta: #e9374f;
  --orange: #f9690e;
  --green: #199d5b;
}

body {
  background-color: $light-blue;
  overflow-x: hidden;
}
strong {
  font-weight: 600;
  color: $dark;
}
.scrollLock {
  overflow: hidden;
}
</style>
