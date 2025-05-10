import type { FC, ReactNode } from "react";

export type SelectOptionData<T = any> = {
  value: T;
  label?: string;
  hidden?: boolean;
  disabled?: boolean;
  children?: SelectOptionData<T>[];
};

export type SelectOption<T> = string | number | SelectOptionData<T>;

export type OptionProps = {
  value: any;
  label?: string;
  isOptionSelected?: boolean;
  disabled?: boolean;
  style?: any;
  multiple?: boolean;
  onSelect?: () => void;
  isIndeterminate?: boolean;
  className?: string;
};

type ExtractStructOption<T> = T extends SelectOptionData ? T["value"] : never;
type ExtractPrimitiveOption<T> = T extends string | number ? T : never;
export type ExtractOption<T> = T extends SelectOption<any>
  ? T extends SelectOptionData<any>
    ? ExtractStructOption<T>
    : ExtractPrimitiveOption<T>
  : never;

export type ExtractValue<T, A extends SelectOption<T>[]> = A[number] extends { value: infer U } ? U : A[number];

export type SelectProps<T, A extends SelectOption<T>[]> = {
  label?: string;
  description?: string;
  options: A[];
  value?: ExtractOption<A[number]> | null;
  defaultValue?: ExtractOption<A[number]> | null;
  validate?: any;
  required?: boolean;
  skip?: boolean;
  labelProps?: any;
  placeholder?: ReactNode;
  className?: string;
  ghost?: boolean;
  width?: string;
  icon?: any;
  fieldProps?: any;
  error?: boolean;
  autoSelectFirst?: boolean;
  searchable?: boolean;
  searchPlaceholder?: string;
  ref?: React.Ref<HTMLSelectElement>;
  selectedValueRenderer?: FC<{ option: A[number]; index: number }>;
  optionRenderer?: FC<{ option: A[number]; index: number }>;
  dropdown?: any;
  testId?: string;
  searchFilter?: (option: any, queryString: string) => boolean;
  onChange?: (value: any) => void | false;
  setValue?: (value: ExtractOption<A>) => void;
  header?: string | FC | JSX.Element;
  multiple?: boolean;
  disabled?: boolean;
  triggerProps?: any;
  isInline?: boolean;
  isLoading?: boolean;
  dataTestid?: string;
  size?: "small" | "medium" | "large";
  onSearch?: (value: string) => void;
  selectFirstIfEmpty?: boolean;
  renderSelected?: (selectedOptions?: A[number][], placeholder?: string) => React.ReactNode | string;
} & SelectVirtualizedProps &
  Omit<React.SelectHTMLAttributes<HTMLSelectElement>, "value" | "placeholder">;

type ToNever<T> = {
  [key in keyof T]?: never;
};

type VirtualizedProps = {
  overscan?: number;
  estimateSize?: number;
  virtualizedTotal?: number;
  rootMargin?: string;
  onBottomReached?: () => void;
  onTopReached?: () => void;
};

type SelectVirtualizedProps =
  | ({
      virtualized?: false;
    } & ToNever<VirtualizedProps>)
  | ({
      virtualized: true;
    } & VirtualizedProps);
