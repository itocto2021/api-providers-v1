// Tipos basados en el contrato OpenAPI de Gapsi Providers

export interface Provider {
  id: string;
  nombre: string;
  razonSocial?: string;
  direccion?: string;
  createdAt: string;
}

export interface ProviderCreate {
  nombre: string;
  razonSocial?: string;
  direccion?: string;
}

export interface ProvidersListResponse {
  total: number;
  size: number;
  page: number;
  items: Provider[];
}

export interface ErrorResponse {
  error: string;
  code: string;
  details?: string;
  timestamp?: string;
}

export interface MessageResponse {
  message: string;
}

export interface AppInfo {
  mensaje: string;
  version: string;
}

// Tipos para manejo de estado
export interface PaginationParams {
  page: number;
  size: number;
}

export interface ApiResponse<T> {
  data?: T;
  error?: ErrorResponse;
  loading: boolean;
}

// Declaraciones de módulos para archivos de imagen
declare module '*.png' {
  const content: string;
  export default content;
}

declare module '*.jpg' {
  const content: string;
  export default content;
}

declare module '*.jpeg' {
  const content: string;
  export default content;
}

declare module '*.gif' {
  const content: string;
  export default content;
}

declare module '*.svg' {
  const content: string;
  export default content;
}