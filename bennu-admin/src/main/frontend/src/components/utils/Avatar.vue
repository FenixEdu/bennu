<template>
  <figure
    :class="{ 'figure--icon': icon }"
    class="figure figure--avatar"
  >
    <img
      v-if="src"
      :class=" {'avatar--hidden-img' : !imageLoaded }"
      :src="src"
      :alt="name"
      @load="imageLoadHandler"
    >
    <div v-if="!icon">
      {{ name.charAt(0) }}
    </div>
  </figure>
</template>

<script>
export default {
  name: 'Avatar',
  props: {
    src: {
      type: String,
      required: false,
      default: undefined
    },
    icon: {
      type: Boolean,
      default: false
    },
    name: {
      type: String,
      required: true
    }
  },
  data () {
    return {
      imageLoaded: false
    }
  },
  methods: {
    imageLoadHandler () {
      this.imageLoaded = true
    }
  }
}
</script>

<style lang="scss">
.profile--left {
  display: flex;
  flex-direction: row;
}

.profile__avatar {
  display: inline-block;
  width: 5rem;
  height: 5rem;

  img.avatar {
    border-radius: 50%;
    width: inherit;
    height: inherit;
  }
}

.avatar--hidden-img {
  display: none;
}
</style>
