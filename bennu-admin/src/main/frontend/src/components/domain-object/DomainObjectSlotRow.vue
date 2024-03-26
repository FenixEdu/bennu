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
        <span class="h5--ssp">{{ domainObjectSlot.name }}:</span> {{ domainObjectSlot.value ?? 'null' }}
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
  }
}
</script>

<style lang="scss" scoped>
.domain-object-slot-row {
  word-wrap: anywhere;
}
</style>
