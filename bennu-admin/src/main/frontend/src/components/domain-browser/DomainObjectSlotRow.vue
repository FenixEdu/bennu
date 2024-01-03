<template>
  <component
    :is="component"
    class="card-row card-row--sm"
  >
    <div class="card-row__text">
      <p>{{ domainObjectSlot.type }}</p>
      <p>
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
    component: {
      type: String,
      required: false,
      default: 'div'
    }
  }
}
</script>

<style lang="scss" scoped>
.slot-field {
  margin-top: 0;
  margin-bottom: 0;
}
</style>
