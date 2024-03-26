<template>
  <component
    :is="component"
    class="card-row card-row--sm domain-object-slot-row"
  >
    <div class="card-row__text">
      <p v-if="showType">
        {{ domainObjectSlot.type }}
      </p>
      <p class="slot-value">
        <span class="h5--ssp">{{ domainObjectSlot.name }}:</span> <pre v-if="isJson">{{ value }}</pre> <template v-else>
          {{ value }}
        </template>
      </p>
    </div>
  </component>
</template>

<script>
export default {
  name: 'DomainObjectSlotsPart',
  props: {
    domainObjectSlot: {
      type: Object,
      required: true
    },
    showType: {
      type: Boolean,
      required: false,
      default: true
    },
    component: {
      type: String,
      required: false,
      default: 'div'
    }
  },
  computed: {
    isJson () {
      try {
        const object = JSON.parse(this.domainObjectSlot.value)
        return !!object && typeof object === 'object'
      } catch (e) {
        return false
      }
    },
    value () {
      return this.domainObjectSlot.value
        ? (this.isJson ? this.prettyJson() : this.domainObjectSlot.value)
        : '-'
    }
  },
  methods: {
    prettyJson () {
      return JSON.stringify(JSON.parse(this.domainObjectSlot.value), null, 2)
    }
  }
}
</script>

<style lang="scss" scoped>
.domain-object-slot-row {
  word-wrap: anywhere;
}
</style>
