<template>
  <svg
    xmlns="http://www.w3.org/2000/svg"
    viewBox="0 0 48 56"
    width="48px"
    class="alert-security"
  >
    <g
      fill="none"
      fill-rule="evenodd"
    >
      <g
        transform="translate(22 19)"
        class="exclamation-mark"
      >
        <circle
          vector-effect="non-scaling-stroke"
          cx="2"
          cy="16.5"
          r="1.5"
          fill="currentColor"
        />
        <path
          vector-effect="non-scaling-stroke"
          stroke="currentColor"
          stroke-linecap="round"
          stroke-width="2"
          d="M2 12V0"
        />
      </g>
      <path
        vector-effect="non-scaling-stroke"
        stroke="currentColor"
        stroke-linecap="round"
        stroke-linejoin="round"
        stroke-width="2"
        d="M24.1949153 55C8.73163842 48.3718412 1 40.5740072 1 31.6064982V9.77256318c6.3612753 0 13.905912-2.80313804 22.6339102-8.4094141l-.000014-.0000218c.3394299-.21802683.7766227-.21075243 1.1086113.01844604C32.8452747 6.97556656 40.2644388 9.77256318 47 9.77256318V31.6064982C47 40.3140794 39.3983051 48.1119134 24.1949153 55z"
      />
    </g>
  </svg>
</template>
<style lang="scss">
.alert-security .explosion {
  transform-origin: center;
  transform: scale(2);
  stroke-dasharray: 22px;
  stroke-dashoffset: 22px;
}

.animated-icon--is-playing .alert-security .exclamation-mark path {
  animation: exclamation-mark 2s infinite;
  stroke-dasharray: 14px;
}

@keyframes exclamation-mark {
  0%,
  10% {
    stroke-dashoffset: 0;
  }
  30%,
  40% {
    stroke-dashoffset: 14px;
  }
  70%,
  100% {
    stroke-dashoffset: 28px;
  }
}

.animated-icon--is-playing .alert-security .exclamation-mark circle {
  animation: exclamation-mark-dot 2s cubic-bezier(0.25, 0.46, 0.45, 0.94) infinite;
  transform-origin: 2px 17px;
}

@keyframes exclamation-mark-dot {
  0%,
  20% {
    transform: scale(1);
  }
  40%,
  50% {
    transform: scale(0);
  }
  80%,
  100% {
    transform: scale(1);
  }
}
</style>
