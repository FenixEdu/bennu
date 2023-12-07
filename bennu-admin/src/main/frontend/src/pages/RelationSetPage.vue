<template>
  <div class="page">
    <header class="section-header page__header">
      <div class="section-header__text">
        <p class="h6 section-header__parent-page">
          <router-link :to="{ name: 'DomainObjectPage', params: { domainObjectId: domainObject.oid } }">
            <span class="i-back-arrow" />
            <span>{{ getDomainObjectName(domainObject) }} - {{ domainObject.oid }}</span>
          </router-link>
        </p>
        <h1 class="section-header__title">
          {{ $t('title', { relationSet: relationName,domainObject: getDomainObjectName(domainObject), oid: domainObject.oid }) }}
        </h1>
        <p>{{ domainObject.type }}</p>
      </div>
    </header>
    <ul
      v-if="relationSet.length > 0"
      class="card"
    >
      <li
        v-for="relation in relationSet"
        :key="relation.value"
        class="card-row card-row--sm"
      >
        <div class="card-row__text">
          <p>
            <span class="h5 h5--ssp">{{ relation.type }}</span> - {{ relation.oid }}
          </p>
        </div>
        <div class="card-row__meta">
          <router-link
            aria-hidden="true"
            tabindex="-1"
            :to="{ name: 'DomainObjectPage', params: { domainObjectId: relation.oid }}"
          >
            <span class="i-arrow-right" />
          </router-link>
        </div>
      </li>
    </ul>
    <empty-state v-else>
      <p slot="title">
        {{ $t('empty-state', { relationName: relationName }) }}
      </p>
    </empty-state>
  </div>
</template>

<script>
import EmptyState from '@/components/EmptyState.vue'

export default {
  components: {
    EmptyState
  },
  props: {
    domainObject: {
      type: Object,
      required: true
    },
    relationSet: {
      type: Array,
      required: true
    }
  },
  computed: {
    relationName () {
      return this.$route.params.relationSetName
    }
  },
  methods: {
    getDomainObjectName (domainObject) {
      return domainObject.type.split('.').pop()
    }
  },
  i18n: {
    messages: {
      pt: {
        title: 'Relation Set "{relationSet}" of "{domainObject}" - {oid}',
        'empty-state': 'Não há relações para o conjunto "{relationName}" neste objeto'
      },
      en: {
        title: 'Relation Set "{relationSet}" of "{domainObject}" - {oid}',
        'empty-state': 'There are no relations for the set "{relationName}" in this object'
      }
    }
  }
}
</script>
